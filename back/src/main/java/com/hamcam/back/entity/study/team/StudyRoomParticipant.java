package com.hamcam.back.entity.study.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudyRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ✅ 참가자 (유저)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * ✅ 참여한 학습방
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    /**
     * ✅ 참가자 입장 시간
     */
    @CreationTimestamp
    private LocalDateTime joinedAt;

    /**
     * ✅ 준비 완료 여부 (문제풀이방에서 사용)
     */
    @Column(nullable = false)
    private boolean isReady;

    /**
     * ✅ 발표자 여부 (문제풀이방에서 사용)
     */
    @Column(nullable = false)
    private boolean isPresenter;

    /**
     * ✅ 목표 시간 달성 여부 (공부시간 경쟁방에서 사용)
     */
    @Column(nullable = false)
    private boolean goalAchieved;

    /**
     * ✅ 종료 후 결과 확인 여부
     */
    @Column(nullable = false)
    private boolean hasConfirmedExit;

    /**
     * ✅ 집중 시간 (초 단위 누적, 서버 측 갱신용)
     */
    @Column(nullable = false)
    private int focusSeconds;

    // === 편의 메서드 ===

    public void markReady() {
        this.isReady = true;
    }

    public void assignPresenter() {
        this.isPresenter = true;
    }

    public void revokePresenter() {
        this.isPresenter = false;
    }

    public void achieveGoal() {
        this.goalAchieved = true;
    }

    public void confirmExit() {
        this.hasConfirmedExit = true;
    }

    public void addFocusSeconds(int seconds) {
        this.focusSeconds += seconds;
    }
}
