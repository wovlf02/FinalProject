package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CHAT_MESSAGE",
        indexes = {
                @Index(name = "IDX_CHAT_ROOM_SENT_AT", columnList = "CHAT_ROOM_ID, SENT_AT")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_message_seq_generator")
    @SequenceGenerator(
            name = "chat_message_seq_generator",
            sequenceName = "CHAT_MESSAGE_SEQ",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID", nullable = false)
    private User sender;

    @Column(name = "CONTENT", length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 50)
    private ChatMessageType type;

    @Column(name = "STORED_FILE_NAME", length = 500)
    private String storedFileName;

    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    @Column(name = "SENT_AT", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    /**
     * 해당 메시지를 읽은 사용자 목록
     */
    @Builder.Default
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRead> reads = new ArrayList<>();

    @PrePersist
    protected void onSend() {
        this.sentAt = LocalDateTime.now();
    }

    public boolean isFileMessage() {
        return type == ChatMessageType.IMAGE || type == ChatMessageType.FILE;
    }

    public String getFileUrl() {
        return storedFileName != null ? "/uploads/chat/" + storedFileName : null;
    }
}
