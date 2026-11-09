package backend.service;

import backend.ai.AiClient;
import backend.domain.Attempt;
import backend.domain.Score;
import backend.domain.Question;
import backend.repository.AttemptRepository;
import backend.repository.QuestionRepository;
import backend.repository.ScoreRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    private final AiClient aiClient;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final ScoreRepository scoreRepository;
    private final MeterRegistry meterRegistry;

    public AnswerService(
            AiClient aiClient,
            QuestionRepository questionRepository,
            AttemptRepository attemptRepository,
            ScoreRepository scoreRepository,
            MeterRegistry meterRegistry) {
        this.aiClient = aiClient;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.scoreRepository = scoreRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public EvaluationResponse evaluateAnswer(Long questionId, String answer) {

        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer cannot be empty");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        long start = System.currentTimeMillis();

        AiClient.ScoreResult result = aiClient.scoreAnswer(
                question.getText(),
                answer,
                question.getCategory()
        );

        long latency = System.currentTimeMillis() - start;

        Attempt attempt = new Attempt();
        attempt.setQuestionId(questionId);
        attempt.setAnswerText(answer);

        Attempt savedAttempt = attemptRepository.save(attempt);

        Score score = new Score();
        score.setAttemptId(savedAttempt.getId());
        score.setClarity(result.clarity());
        score.setSpecificity(result.specificity());
        score.setDepth(result.depth());
        score.setFeedback(result.feedback());
        score.setImprovementTip(result.improvementTip());
        score.setModel("gemini-3.1-flash-lite");;
        score.setLatencyMs(latency);
        score.setTokensIn(0);
        score.setTokensOut(0);

        scoreRepository.save(score);

        meterRegistry.counter("interview.answers.evaluated").increment();
        meterRegistry.timer("interview.answer.evaluation.duration")
                .record(java.time.Duration.ofMillis(latency));

        double overall = (
                result.clarity() +
                        result.specificity() +
                        result.depth()
        ) / 3.0;

        return new EvaluationResponse(
                savedAttempt.getId(),
                questionId,
                result.clarity(),
                result.specificity(),
                result.depth(),
                overall,
                result.feedback(),
                result.improvementTip(),
                result.supportingQuotes(),
                latency
        );
    }

    public record EvaluationResponse(
            Long attemptId,
            Long questionId,
            int clarity,
            int specificity,
            int depth,
            double overallScore,
            String feedback,
            String improvementTip,
            java.util.List<String> supportingQuotes,
            long latencyMs
    ) {}
}