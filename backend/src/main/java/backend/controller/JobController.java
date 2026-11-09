package backend.controller;

import backend.repository.QuestionRepository;
import backend.domain.Question;
import java.util.List;
import backend.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class JobController {

    private final JobService jobService;

    private final QuestionRepository questionRepository;

    public JobController(JobService jobService,  QuestionRepository questionRepository) {
        this.jobService = jobService;
        this.questionRepository = questionRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobService.JobCreationResponse createJob(
            @RequestBody CreateJobRequest request) {

        return jobService.createJob(request.jobDescription());
    }

    @GetMapping("/{jobId}/questions")
    public List<QuestionResponse> getQuestions(@PathVariable Long jobId) {

        return questionRepository.findByJobProfileId(jobId)
                .stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getText(),
                        q.getCategory(),
                        q.getSkill()
                ))
                .toList();
    }

    public record QuestionResponse(
            Long id,
            String text,
            String category,
            String skill
    ) {}

    public record CreateJobRequest(String jobDescription) {}
}