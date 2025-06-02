package com.hamcam.back.entity.dashboard;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공부한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /** 공부한 날짜 */
    @Column(nullable = false)
    private LocalDate studyDate;

    /** 집중 시간 (분) */
    private int focusMinutes;

    /** 총 공부 시간 (분) */
    private int totalMinutes;

    /** 집중률 (%) */
    private double focusRate;

    /** 정답률 (%) - 모의고사 등과 연동 가능 */
    private double accuracy;

    /** 과목명 (선택) */
    private String subject;

    /** 오답률 또는 정답률 (%) */
    private double correctRate;

    /** 경고 횟수 (자리이탈/졸음 감지 등) */
    private int warningCount;

    // ===== 유틸 메서드 =====

    public int getDurationMinutes() {
        return totalMinutes;
    }

    public int getRoundedFocusRate() {
        return (int) Math.round(focusRate);
    }

    public int getRoundedAccuracy() {
        return (int) Math.round(accuracy);
    }

    public int getRoundedCorrectRate() {
        return (int) Math.round(correctRate);
    }
}
