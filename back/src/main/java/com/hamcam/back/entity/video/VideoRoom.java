// src/main/java/com/hamcam/back/entity/video/VideoRoom.java
package com.hamcam.back.entity.video;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "video_room")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class VideoRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long hostId;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RoomType type;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(length = 100)
    private String password;

    @Column
    private Integer targetTime;  // FOCUS 방용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status;

    @OneToMany(
      mappedBy = "room",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<VideoRoomParticipant> participants = new ArrayList<>();

    /** 편의 메서드: 참가자 추가 */
    public void addParticipant(VideoRoomParticipant participant) {
        participants.add(participant);
        participant.setRoom(this);
    }

    /** 편의 메서드: 참가자 제거 */
    public void removeParticipant(VideoRoomParticipant participant) {
        participants.remove(participant);
        participant.setRoom(null);
    }
}
