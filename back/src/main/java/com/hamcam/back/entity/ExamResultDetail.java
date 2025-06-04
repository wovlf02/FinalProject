package com.hamcam.back.entity;

import jakarta.persistence.*;
import com.hamcam.back.entity.exam.ExamResult;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ExamResultDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_result_id")
    @JsonIgnore
    private ExamResult examResult;

    private Long problemId;        // 출제된 문제 ID
    private String userAnswer;     // 사용자가 제출한 답
    private String correctAnswer;  // 정답
    private Boolean correct;       // 정오답 여부

    // --- Getter/Setter ---
    public Long getId() { return id; }
    public ExamResult getExamResult() { return examResult; }
    public void setExamResult(ExamResult examResult) { this.examResult = examResult; }
    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
}
