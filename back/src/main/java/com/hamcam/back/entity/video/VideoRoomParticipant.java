// src/main/java/com/hamcam/back/entity/video/VideoRoomParticipant.java
package com.hamcam.back.entity.video;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "video_room_participant",
  uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VideoRoomParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private VideoRoom room;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public VideoRoomParticipant(VideoRoom room, Long userId) {
        this.room = room;
        this.userId = userId;
    }
}
