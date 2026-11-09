package backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_id", nullable = false, unique = true)
    private Long attemptId;

    @Column(nullable = false)
    private Integer clarity;

    @Column(nullable = false)
    private Integer specificity;

    @Column(nullable = false)
    private Integer depth;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "improvement_tip", nullable = false, columnDefinition = "TEXT")
    private String improvementTip;

    private String model;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "tokens_in")
    private Integer tokensIn;

    @Column(name = "tokens_out")
    private Integer tokensOut;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Score() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long id) { this.attemptId = id; }
    public Integer getClarity() { return clarity; }
    public void setClarity(Integer value) { this.clarity = value; }
    public Integer getSpecificity() { return specificity; }
    public void setSpecificity(Integer value) { this.specificity = value; }
    public Integer getDepth() { return depth; }
    public void setDepth(Integer value) { this.depth = value; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String value) { this.feedback = value; }
    public String getImprovementTip() { return improvementTip; }
    public void setImprovementTip(String value) { this.improvementTip = value; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Long value) { this.latencyMs = value; }
    public Integer getTokensIn() { return tokensIn; }
    public void setTokensIn(Integer value) { this.tokensIn = value; }
    public Integer getTokensOut() { return tokensOut; }
    public void setTokensOut(Integer value) { this.tokensOut = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}