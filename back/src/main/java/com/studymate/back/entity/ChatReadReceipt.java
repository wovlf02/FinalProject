package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ChatReadReceipt Entity (메시지 읽음 확인)
 * 사용자가 특정 메시지를 읽었는지 여부를 저장하는 JPA 엔티티
 * users 테이블과 연관 (어떤 사용자가 읽었는지 저장)
 * chat_messages 테이블과 연관 (어떤 메시지를 읽었는지 저장)
 */
@Entity
@Table(name = "chat_read_receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatReadReceipt {

    /**
     * 읽음 확인한 메시지 (ChatMessage)
     * Many-to-One 관계 (한 메시지는 여러 사용자가 읽을 수 있음)
     * 메시지 삭제 시, 읽음 정보도 삭제됨 (CASCADE 설정)
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false, foreignKey = @ForeignKey(name = "fk_read_message"))
    private ChatMessage message;

    /**
     * 읽음 확인한 사용자 (User)
     * Many-to-One 관계 (한 사용자는 여러 메시지를 읽을 수 있음)
     * 사용자 삭제 시, 읽음 정보는 NULL로 유지 (SET NULL)
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name = "fk_read_user"))
    private User user;

    /**
     * 읽음 호가인 시각 (read_at)
     * Default: 현재 시각
     * 사용자가 메시지를 읽은 시각 저장
     */
    @CreationTimestamp
    @Column(name = "read_at", nullable = false, updatable = false)
    private LocalDateTime readAt;
}
