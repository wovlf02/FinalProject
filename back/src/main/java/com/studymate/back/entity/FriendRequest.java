package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * FriendRequest Entity (친구 요청)
 * 사용자 간 친구 요청을 관리하는 JPA 엔티티
 * users 테이블과 연관 (보낸 사용자, 받은사용자 저장)
 * 친구 요청 상태 (대기, 수락, 거절) 저장
 */
@Entity
@Table(name = "friend_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    /**
     * 친구 요청 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    /**
     * 친구 요청을보낸 사용자 (Sender)
     * Many-to-One 관계 (한 사용자는 여러 친구 요청을 보낼 수 있음)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_friend_request_sender"))
    private User sender;

    /**
     * 친구 요청을 받은 사용자 (Receiver)
     * Many-to-One 관계 (한 사용자는 여러 친구 요청을 받을 수 있음)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false, foreignKey = @ForeignKey(name = "fk_friend_request_receiver"))
    private User receiver;

    /**
     * 친구 요청 상태 (Status)
     * pending: 대기 / accepted: 수락 / rejected: 거절 값만 허용
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    /**
     * 친구 요청 생성 시각 (created_at)
     * Default: 현재 시각
     * 친구 요청이 전송될 때 자동 저장
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
