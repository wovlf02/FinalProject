package com.hamcam.back.chat.entity;

import com.hamcam.back.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * [ChatMessage 엔티티 클래스]
 * 
 * 채팅방 내에서 사용자들이 주고받은 채팅 메시지를 저장하는 엔티티
 * 텍스트 기반 메시지뿐만 아니라 파일 첨부(이미지, 문서 등)도 가능하며,
 * WebSocket으로 전송되는 메시지의 영속화를 담당함
 * 
 * 메시지 내용, 발신자, 채팅방, 전송 시각 등을 모두 포함
 */
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    /**
     * [PK] 채팅 메시지 고유 ID
     * 각 메시지를 유일하게 식별할 수 있는 자동 증가 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    /**
     * 채팅방 정보 (ManyToOne)
     * 
     * 이 메시지가 속한 채팅방을 참조
     * 한의 채팅방에 여러 메시지가 연결될 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    /**
     * 메시지를 보낸 사용자 (ManyToOne)
     * 
     * 실제 메시지를 발신한 유저를 참조
     * 유저의 nickname 등을 통해 프론트에 표시 가능
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * 채팅 텍스트 메시지 내용
     * 
     * 사용자가 입력한 일반 텍스트 내용 (이모지 포함 가능)
     * 첨부파일이 있는 경우 비워질 수 있음 (선택 입력)
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 첨부파일 바이너리 데이터
     * 
     * 사용자가 보낸 이미지, 문서 등의 실제 파일 데이터
     * 보안 및 저장 방식에 따라 S3 등 외부 저장소 사용 고려 가능
     */
    @Lob
    @Column(name = "file")
    private byte[] file;

    /**
     * 첨부파일의 MIME 타입
     * 
     * 파일 식별을 위한 MIME 타입 (ex. image/jpeg, application/pdf 등)
     * 프론트엔드에서 미디어를 동적으로 렌더링하는 데 활용
     */
    @Column(name = "file_type", length = 50)
    private String fileType;

    /**
     * 메시지 전송 시각
     * 
     * 사용자가 메시지를 전송한 정확한 시간
     * 자동 생성됨 (insert 시점의 현재 시간 기록)
     */
    @CreationTimestamp
    @Column(name = "send_at", nullable = false, updatable = false)
    private LocalDateTime sendAt;
}
