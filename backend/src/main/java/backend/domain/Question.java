package backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_profile_id", nullable = false)
    private Long jobProfileId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 100)
    private String skill;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Question() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getJobProfileId() { return jobProfileId; }
    public void setJobProfileId(Long id) { this.jobProfileId = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}