package com.hamcam.back.entity.study;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate studyDate;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer focusRate;

    @Column(nullable = false)
    private Integer accuracy;

    @Column(nullable = false)
    private Integer correctRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyType studyType;

    /**
     * 과목명 (예: 수학, 영어 등)
     */
    @Column(nullable = false, length = 50)
    private String subject;

    /**
     * 집중 방해(경고) 횟수
     */
    @Column(nullable = false)
    private int warningCount;

    /**
     * ✅ 단원명 (예: "기출 분석", "개념 정리" 등)
     */
    @Column(nullable = false, length = 100)
    private String unitName;

    public enum StudyType {
        PERSONAL,
        TEAM
    }
}
