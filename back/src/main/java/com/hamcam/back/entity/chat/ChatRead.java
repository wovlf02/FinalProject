package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHAT_READ",
        uniqueConstraints = @UniqueConstraint(columnNames = {"MESSAGE_ID", "USER_ID"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRead {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_read_seq_generator")
    @SequenceGenerator(name = "chat_read_seq_generator", sequenceName = "CHAT_READ_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private ChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "READ_AT", nullable = false)
    private LocalDateTime readAt;

    public static ChatRead create(ChatMessage message, User user) {
        return ChatRead.builder()
                .message(message)
                .user(user)
                .readAt(LocalDateTime.now())
                .build();
    }
}
