package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 친구 요청(FriendRequest) 엔티티
 * <p>
 * 사용자가 다른 사용자에게 친구 요청을 보낸 기록을 나타냅니다.
 * 요청이 수락되면 Friend 엔티티로 전환되며, 거절되거나 삭제될 수도 있습니다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @PrePersist
    protected void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }
}

