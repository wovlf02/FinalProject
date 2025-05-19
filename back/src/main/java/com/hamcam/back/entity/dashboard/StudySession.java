package com.hamcam.back.entity.dashboard;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 공부 날짜 (YYYY-MM-DD)
     */
    @Column(nullable = false)
    private LocalDate studyDate;

    /**
     * 공부 시작 시각
     */
    @Column(nullable = false)
    private LocalDateTime startedAt;

    /**
     * 공부 종료 시각
     */
    @Column(nullable = false)
    private LocalDateTime endedAt;

    /**
     * 총 공부 시간 (분 단위)
     */
    @Column(nullable = false)
    private Integer durationMinutes;

    /**
     * 집중률 (0 ~ 100)
     */
    @Column(nullable = false)
    private Integer focusRate;

    /**
     * 정확도 (0 ~ 100)
     */
    @Column(nullable = false)
    private Integer accuracy;

    /**
     * 정답률 (0 ~ 100)
     */
    @Column(nullable = false)
    private Integer correctRate;

    /**
     * 학습 유형 (PERSONAL / TEAM)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyType studyType;

    public enum StudyType {
        PERSONAL,
        TEAM
    }

    /**
     * 과목명 (예: 수학, 영어 등)
     */
    @Column(nullable = false, length = 50)
    private String subject;
}
