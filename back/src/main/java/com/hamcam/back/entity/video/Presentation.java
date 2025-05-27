package com.hamcam.back.entity.video;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ✅ 발표 기록 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 발표자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User presenter;

    /** 해당 방 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private VideoRoom room;

    /** 발표 성공 여부 */
    @Column(nullable = false)
    private boolean passed;

    /** 발표 일시 */
    @Column(nullable = false)
    private LocalDateTime presentedAt;

    @PrePersist
    public void prePersist() {
        this.presentedAt = LocalDateTime.now();
    }
}
