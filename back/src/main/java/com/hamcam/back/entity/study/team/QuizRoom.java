package com.hamcam.back.entity.study.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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

    @Column(nullable = false)
    private Long problemId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private String difficulty;

    @Builder
    public QuizRoom(String title,
                    String password,
                    String inviteCode,
                    Long problemId,
                    String subject,
                    int grade,
                    int month,
                    String difficulty,
                    User host) {
        super(title, password, inviteCode, RoomType.QUIZ, host);
        this.problemId = problemId;
        this.subject = subject;
        this.grade = grade;
        this.month = month;
        this.difficulty = difficulty;
    }
}
