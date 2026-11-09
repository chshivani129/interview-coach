package backend.controller;

import backend.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/{questionId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerService.EvaluationResponse submitAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerRequest request) {

        return answerService.evaluateAnswer(
                questionId,
                request.answer()
        );
    }

    public record AnswerRequest(String answer) {}
}