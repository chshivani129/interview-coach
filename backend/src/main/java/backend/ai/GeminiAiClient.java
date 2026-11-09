package backend.ai;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
public class GeminiAiClient implements AiClient {

    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public GeminiAiClient(
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model}") String model,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    private JsonNode askGemini(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is missing.");
        }

        Map<String, Object> request = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"
                )
        );

        JsonNode response = restClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/"
                        + model + ":generateContent")
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JsonNode.class);

        String result = response.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText();

        if (result.isBlank()) {
            throw new IllegalStateException("Gemini returned an empty response.");
        }

        try {
            return objectMapper.readTree(result);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse Gemini's JSON response.", e);
        }
    }

    @Override
    public JdAnalysis analyzeJobDescription(String jd) {
        String prompt = """
                Analyze this job description and return JSON with:
                title, seniority, skills (array of strings), themes (array of strings).
                Identify the actual skills and seniority from the description.
                Job description:
                """ + jd;

        JsonNode json = askGemini(prompt);

        return new JdAnalysis(
                json.path("title").asText(),
                json.path("seniority").asText(),
                objectMapper.convertValue(
                        json.path("skills"),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, String.class)),
                objectMapper.convertValue(
                        json.path("themes"),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, String.class))
        );
    }

    @Override
    public List<GeneratedQuestion> generateQuestions(
            String title, List<String> skills, int count) {

        String prompt = """
                Generate %d interview questions for this role: %s.
                Focus on these skills: %s.
                Return JSON in this format:
                {"questions":[{"text":"question","category":"TECHNICAL","skill":"Java"}]}
                Questions should be realistic, specific, and suitable for the role's seniority.
                """.formatted(count, title, String.join(", ", skills));

        JsonNode json = askGemini(prompt);

        return objectMapper.convertValue(
                json.path("questions"),
                objectMapper.getTypeFactory().constructCollectionType(
                        List.class, GeneratedQuestion.class));
    }

    @Override
    public ScoreResult scoreAnswer(
            String question, String answer, String category) {

        String prompt = """
                Evaluate this candidate's interview answer.

                Question: %s
                Category: %s
                Candidate answer: %s

                Score each dimension from 1 to 5:
                - clarity: how clearly the candidate communicates
                - specificity: how well they use concrete examples and details
                - depth: how thoroughly they explain their experience and decisions

                Return JSON with:
                clarity, specificity, depth, feedback, improvementTip,
                supportingQuotes (array of short exact quotes from the answer).

                Be fair and constructive. Do not give high scores just because
                the answer is long.
                """.formatted(question, category, answer);

        JsonNode json = askGemini(prompt);

        return new ScoreResult(
                json.path("clarity").asInt(),
                json.path("specificity").asInt(),
                json.path("depth").asInt(),
                json.path("feedback").asText(),
                json.path("improvementTip").asText(),
                objectMapper.convertValue(
                        json.path("supportingQuotes"),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, String.class))
        );
    }
}