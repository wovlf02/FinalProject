package com.hamcam.back.entity.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티 (MySQL 기반)
 */
@Entity
@Table(name = "chat_room", // ✅ MySQL은 소문자 테이블명 선호
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Oracle의 SEQUENCE → MySQL IDENTITY
    private Long id;

    /**
     * 채팅방 이름 (1:1 채팅은 null 가능)
     */
    @Column(name = "name", length = 255)
    private String name;

    /**
     * 채팅방 유형 (DIRECT, GROUP, STUDY, POST 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ChatRoomType type;

    /**
     * 연동 리소스 ID (게시글, 그룹 등)
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 참여자 목록
     */
    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    /**
     * 메시지 목록
     */
    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    /**
     * 최근 메시지 내용 (미리보기용)
     */
    @Column(name = "last_message", length = 1000)
    private String lastMessage;

    /**
     * 최근 메시지 시각
     */
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    /**
     * 생성 시각 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 최근 메시지 업데이트
     */
    public void updateLastMessage(String message, LocalDateTime time) {
        this.lastMessage = message;
        this.lastMessageAt = time;
    }
}
