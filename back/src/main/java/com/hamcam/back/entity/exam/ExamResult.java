package com.hamcam.back.entity.exam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.hamcam.back.entity.ExamResultDetail;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "exam_result", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "subject", "date"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "name")
    private String name;

    @Column(name = "score")
    private int score;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "solve_time")
    private int solveTime;

    @Column(name = "question_count")
    private int questionCount;

    @Column(name = "avg")
    private int avg;

    @Column(name = "user_rank")
    private String rank;

    @Column(name = "date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamResultDetail> details = new ArrayList<>();

    // --- Getter/Setter ---
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getSolveTime() { return solveTime; }
    public void setSolveTime(int solveTime) { this.solveTime = solveTime; }
    public List<ExamResultDetail> getDetails() { return details; }
    public void setDetails(List<ExamResultDetail> details) { this.details = details; }
} 