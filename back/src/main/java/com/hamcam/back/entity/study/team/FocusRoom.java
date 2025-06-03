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
 * ✅ 공부시간 경쟁방 엔티티
 * StudyRoom의 하위 클래스 (상속 구조)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("FOCUS")
public class FocusRoom extends StudyRoom {

    /** 목표 공부 시간 (단위: 분) */
    @Column(nullable = false)
    private int targetTime;

    @Builder
    public FocusRoom(String title,
                     String password,
                     String inviteCode,
                     int targetTime,
                     User host) {
        // ✅ RoomType.FOCUS 명시적으로 전달
        super(title, password, inviteCode, RoomType.FOCUS, host);
        this.targetTime = targetTime;
    }
}
