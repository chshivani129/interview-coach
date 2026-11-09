package backend.service;

import backend.domain.Question;
import backend.domain.Attempt;
import backend.domain.Score;
import backend.repository.JobProfileRepository;
import backend.repository.QuestionRepository;
import backend.repository.AttemptRepository;
import backend.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final JobProfileRepository jobRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final ScoreRepository scoreRepository;

    public DashboardService(
            JobProfileRepository jobRepository,
            QuestionRepository questionRepository,
            AttemptRepository attemptRepository,
            ScoreRepository scoreRepository) {
        this.jobRepository = jobRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.scoreRepository = scoreRepository;
    }

    public DashboardResponse getDashboard(Long jobId) {

        if (!jobRepository.existsById(jobId)) {
            throw new IllegalArgumentException("Job not found");
        }

        List<Question> questions =
                questionRepository.findByJobProfileId(jobId);

        List<Score> allScores =
                scoreRepository.findAllByOrderByCreatedAtDesc();

        Map<String, List<Double>> skillScores = new HashMap<>();

        int answeredQuestions = 0;
        List<Double> overallScores = new ArrayList<>();

        for (Question question : questions) {

            List<Attempt> attempts =
                    attemptRepository.findByQuestionId(question.getId());

            if (!attempts.isEmpty()) {
                answeredQuestions++;
            }

            for (Attempt attempt : attempts) {

                allScores.stream()
                        .filter(s -> s.getAttemptId().equals(attempt.getId()))
                        .findFirst()
                        .ifPresent(score -> {

                            double average = (
                                    score.getClarity()
                                            + score.getSpecificity()
                                            + score.getDepth()
                            ) / 3.0;

                            overallScores.add(average);

                            skillScores.computeIfAbsent(
                                    question.getSkill(),
                                    k -> new ArrayList<>()
                            ).add(average);
                        });
            }
        }

        double averageScore = overallScores.isEmpty()
                ? 0
                : overallScores.stream()
                .mapToDouble(Double::doubleValue)
                .average().orElse(0);

        List<SkillPerformance> performance = skillScores.entrySet()
                .stream()
                .map(entry -> new SkillPerformance(
                        entry.getKey(),
                        entry.getValue().stream()
                                .mapToDouble(Double::doubleValue)
                                .average().orElse(0)
                ))
                .sorted(Comparator.comparingDouble(SkillPerformance::averageScore))
                .toList();

        List<String> weakSkills = performance.stream()
                .filter(s -> s.averageScore() < 3.5)
                .map(SkillPerformance::skill)
                .toList();

        return new DashboardResponse(
                questions.size(),
                answeredQuestions,
                questions.size() - answeredQuestions,
                overallScores.size(),
                Math.round(averageScore * 100.0) / 100.0,
                performance,
                weakSkills
        );
    }

    public record SkillPerformance(
            String skill,
            double averageScore
    ) {}

    public record DashboardResponse(
            int totalQuestions,
            int answeredQuestions,
            int unansweredQuestions,
            int totalEvaluations,
            double averageScore,
            List<SkillPerformance> skillPerformance,
            List<String> weakSkills
    ) {}
}