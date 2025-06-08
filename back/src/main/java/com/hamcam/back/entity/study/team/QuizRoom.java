package com.hamcam.back.entity.study.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ 문제풀이방(Quiz Study Room) 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("QUIZ")
public class QuizRoom extends StudyRoom {

    @Column(nullable = true)
    private Long problemId;

    @Column(nullable = true)
    private String subject;

    @Column(nullable = true)
    private Integer grade;

    @Column(nullable = true)
    private Integer month;

    @Column(nullable = true)
    private String difficulty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_problem_id")
    private Problem currentProblem;


    @Builder
    public QuizRoom(String title,
                    String password,
                    String inviteCode,
                    Long problemId,
                    String subject,
                    Integer grade,
                    Integer month,
                    String difficulty,
                    User host) {
        // ✅ RoomType.QUIZ 명시적으로 전달
        super(title, password, inviteCode, RoomType.QUIZ, host);
        this.problemId = problemId;
        this.subject = subject;
        this.grade = grade;
        this.month = month;
        this.difficulty = difficulty;
    }
}
