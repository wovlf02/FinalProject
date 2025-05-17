package com.hamcam.back.entity.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방 엔티티 (Oracle Express 기반)
 */
@Entity
@Table(name = "CHAT_ROOM",
        indexes = {
                @Index(name = "IDX_CHATROOM_TYPE_REF", columnList = "TYPE, REFERENCE_ID"),
                @Index(name = "IDX_CHATROOM_LAST_MESSAGE_AT", columnList = "LAST_MESSAGE_AT")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_seq_generator")
    @SequenceGenerator(
            name = "chat_room_seq_generator",
            sequenceName = "CHAT_ROOM_SEQ",
            allocationSize = 1
    )
    private Long id;

    /** 채팅방 이름 (1:1 채팅일 경우 null 가능) */
    @Column(name = "NAME", length = 255)
    private String name;

    /** 대표 프로필 이미지 (그룹/스터디 채팅 등에서 사용) */
    @Column(name = "REPRESENTATIVE_IMAGE_URL", length = 500)
    private String representativeImageUrl;

    /** 채팅방 유형 (DIRECT, GROUP, STUDY, POST 등) */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 50)
    private ChatRoomType type;

    /** 연동 리소스 ID (게시글, 그룹 등) */
    @Column(name = "REFERENCE_ID")
    private Long referenceId;

    /** 최근 메시지 내용 (미리보기용) */
    @Column(name = "LAST_MESSAGE", length = 1000)
    private String lastMessage;

    /** 최근 메시지 전송 시각 */
    @Column(name = "LAST_MESSAGE_AT")
    private LocalDateTime lastMessageAt;

    /** 채팅방 생성 시각 */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ====== 연관관계 ======

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    // ====== 메서드 ======

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateLastMessage(String message, LocalDateTime time) {
        this.lastMessage = message;
        this.lastMessageAt = time;
    }
}
