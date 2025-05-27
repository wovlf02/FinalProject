package com.hamcam.back.entity.video;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * ✅ 팀 학습방 참가자 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 참가자 유저 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** 참가한 방 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private VideoRoom room;

    /** SFU signaling 식별자 */
    @Column(length = 100)
    private String socketId;

    /** 발표자 여부 */
    @Column(nullable = false)
    private boolean isPresenter;

    /** 누적 집중 시간 (FOCUS 모드) */
    @Column(nullable = false)
    private int focusTime;

    public void addFocusTime(int seconds) {
        this.focusTime += seconds;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public void setPresenter(boolean presenter) {
        this.isPresenter = presenter;
    }
}
