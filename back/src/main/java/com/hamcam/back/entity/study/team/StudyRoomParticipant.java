package com.hamcam.back.entity.study.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

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

    /** 참가자 유저 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 참가한 방 (StudyRoom을 상속한 FocusRoom/QuizRoom 포함) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private StudyRoom room;

    @Column(nullable = false)
    private boolean isHost = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public static StudyRoomParticipant of(User user, StudyRoom room, boolean isHost) {
        return StudyRoomParticipant.builder()
                .user(user)
                .room(room)
                .isHost(isHost)
                .build();
    }
}
