package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 차단(FriendBlock) 엔티티
 * <p>
 * 친구 여부와 관계없이 사용자가 특정 사용자를 차단한 내역을 저장합니다.
 * 차단된 사용자는 나에게 친구 요청, 채팅, 피드 노출 등이 제한됩니다.
 * </p>
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"})
)
public class FriendBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 차단한 사용자 (blocker)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    /**
     * 차단당한 사용자 (blocked)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

    /**
     * 차단 시각
     */
    private LocalDateTime blockedAt;
}
