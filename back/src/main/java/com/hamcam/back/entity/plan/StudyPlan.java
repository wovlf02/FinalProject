package com.hamcam.back.entity.plan;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_plan")
public class StudyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String subject;

    private String grade;

    private int weeks;

    private String units; // 예: "문자와 식, 방정식과 부등식"

    @Column(name = "plan_content", columnDefinition = "TEXT")
    @JsonProperty("planContent")
    private String planContent;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public int getWeeks() { return weeks; }
    public void setWeeks(int weeks) { this.weeks = weeks; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }

    @JsonProperty("planContent")
    public String getPlanContent() { return planContent; }
    public void setPlanContent(String planContent) { this.planContent = planContent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
