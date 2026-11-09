package backend.service;

import backend.ai.AiClient;
import backend.domain.JobProfile;
import backend.domain.Question;
import backend.repository.JobProfileRepository;
import backend.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final AiClient aiClient;
    private final JobProfileRepository jobRepository;
    private final QuestionRepository questionRepository;

    public JobService(
            AiClient aiClient,
            JobProfileRepository jobRepository,
            QuestionRepository questionRepository) {
        this.aiClient = aiClient;
        this.jobRepository = jobRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public JobCreationResponse createJob(String jobDescription) {

        if (jobDescription == null || jobDescription.isBlank()) {
            throw new IllegalArgumentException("Job description cannot be empty");
        }

        // 1. Analyze the job description using AI
        AiClient.JdAnalysis analysis =
                aiClient.analyzeJobDescription(jobDescription);

        // 2. Save the job profile
        JobProfile job = new JobProfile();
        job.setTitle(analysis.title());
        job.setRawJd(jobDescription);
        job.setExtractedSkills(String.join(", ", analysis.skills()));

        JobProfile savedJob = jobRepository.save(job);

        // 3. Generate interview questions
        List<AiClient.GeneratedQuestion> generatedQuestions =
                aiClient.generateQuestions(
                        analysis.title(),
                        analysis.skills(),
                        10
                );

        // 4. Save generated questions
        List<Question> savedQuestions = generatedQuestions.stream()
                .map(generated -> {
                    Question question = new Question();
                    question.setJobProfileId(savedJob.getId());
                    question.setText(generated.text());
                    question.setCategory(generated.category());
                    question.setSkill(generated.skill());
                    return questionRepository.save(question);
                })
                .toList();

        return new JobCreationResponse(
                savedJob.getId(),
                savedJob.getTitle(),
                analysis.seniority(),
                analysis.skills(),
                savedQuestions.size()
        );
    }

    public record JobCreationResponse(
            Long jobId,
            String title,
            String seniority,
            List<String> skills,
            int questionCount
    ) {}
}