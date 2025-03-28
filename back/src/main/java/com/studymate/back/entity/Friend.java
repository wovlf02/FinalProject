package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Friend Entity (친구 목록)
 * 사용자 간 친구 관계를 저장하는 JPA 엔티티
 * users 테이블과 연관 (사용자 ID, 친구 ID 저장
 */
@Entity
@Table(name = "friends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    /**
     * 사용자 (User)
     * Many-to-One 관계 (한 사용자는 여러 친구를 가질 수 있음)
     * 친구 관계는양방향 (user_id와 friend_id 조합으로 복합 키 설정)
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_friend_user"))
    private User user;

    /**
     * 친구 사용자 (User)
     * Many-to-One 관계 (친구 관계를저장)
     * 동일한 친구 관계가 중복되지 않도록 복합 키 설정
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false, foreignKey = @ForeignKey(name = "fk_friend_friend"))
    private User friend;

    /**
     * 친구 관계 생성 시각 (created_at)
     * Default: 현재 시각
     * 사용자가 친구 추가할 때 자동 저장
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
