package backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_profile")
public class JobProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "raw_jd", nullable = false, columnDefinition = "TEXT")
    private String rawJd;

    @Column(name = "extracted_skills", columnDefinition = "TEXT")
    private String extractedSkills;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public JobProfile() {}

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRawJd() { return rawJd; }
    public void setRawJd(String rawJd) { this.rawJd = rawJd; }
    public String getExtractedSkills() { return extractedSkills; }
    public void setExtractedSkills(String skills) {
        this.extractedSkills = skills;
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
}