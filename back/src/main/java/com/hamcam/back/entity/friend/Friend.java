package com.hamcam.back.entity.friend;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 친구(Friend) 엔티티
 * <p>
 * 두 명의 사용자가 친구 관계임을 나타냅니다.
 * 하나의 레코드는 요청자와 수락자의 관계를 저장하며,
 * 실제로는 양방향 관계지만 DB에는 단방향으로 저장됩니다.
 * </p>
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 1 (친구 관계의 한쪽)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    /**
     * 사용자 2 (친구 관계의 다른 한쪽)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    /**
     * 친구 관계 형성 시각
     */
    private String createdAt;
}
