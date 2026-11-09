package backend.ai;

import java.util.List;

public interface AiClient {

    JdAnalysis analyzeJobDescription(String jobDescription);

    List<GeneratedQuestion> generateQuestions(
            String title,
            List<String> skills,
            int count
    );

    ScoreResult scoreAnswer(
            String question,
            String answer,
            String category
    );

    record JdAnalysis(
            String title,
            String seniority,
            List<String> skills,
            List<String> themes
    ) {}

    record GeneratedQuestion(
            String text,
            String category,
            String skill
    ) {}

    record ScoreResult(
            int clarity,
            int specificity,
            int depth,
            String feedback,
            String improvementTip,
            List<String> supportingQuotes
    ) {}
}