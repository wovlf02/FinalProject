package com.hamcam.back.entity.chat;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티
 * <p>
 * 채팅방에서 사용자가 전송한 텍스트, 이미지, 파일 메시지를 저장합니다.
 * 파일 메시지의 경우 실제 서버에 저장된 파일명을 함께 보관합니다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 채팅방 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    /**
     * 메시지를 보낸 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    /**
     * 메시지 본문
     */
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE)
     */
    private String type;

    /**
     * 전송 시각
     */
    private LocalDateTime sentAt;

    /**
     * 저장된 첨부파일 이름 (type이 FILE인 경우 사용)
     */
    @Column(name = "stored_file_name")
    private String storedFileName;
}
