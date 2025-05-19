package com.hamcam.back.entity.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티 (MySQL 호환)
 */
@Entity
@Table(name = "chat_room",
        indexes = {
                @Index(name = "idx_chatroom_type_ref", columnList = "type, reference_id"),
                @Index(name = "idx_chatroom_last_message_at", columnList = "last_message_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    /**
     * 기본키 - MySQL에서는 AUTO_INCREMENT 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 채팅방 이름 (1:1일 경우 null 가능)
     */
    @Column(length = 255)
    private String name;

    /**
     * 대표 이미지 URL
     */
    @Column(name = "representative_image_url", length = 500)
    private String representativeImageUrl;

    /**
     * 채팅방 유형 (DIRECT, GROUP, STUDY, POST 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatRoomType type;

    /**
     * 연동 리소스 ID (게시글, 그룹 등)
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * 마지막 메시지 (미리보기용)
     */
    @Column(name = "last_message", length = 1000)
    private String lastMessage;

    /**
     * 마지막 메시지 시각
     */
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    /**
     * 채팅방 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ====== 연관관계 ======

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    // ====== 콜백 ======

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ====== 유틸 메서드 ======

    public void updateLastMessage(String message, LocalDateTime time) {
        this.lastMessage = message;
        this.lastMessageAt = time;
    }
}
