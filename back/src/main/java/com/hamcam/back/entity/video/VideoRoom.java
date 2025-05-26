package com.hamcam.back.entity.video;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "video_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <= DB의 id 컬럼과 이름 일치

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
