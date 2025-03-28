package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ChatMessage Entity (채팅 메시지)
 * 채팅방에서 주고받은 메시지를 저장하는 JPA 엔티티
 * chat_rooms 테이블과 관련 (어떤 채팅방에서 보낸 메시지인지 저장)
 * users 테이블과 관련 (메시지를 보낸 사용자 저장)
 * 텍스트, 파일, 이미지 등의 메시지를 저장 가능
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
     * 메시지 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    /**
     * 해당 메시지가 속한 채팅방 (ChatRoom)
     * Many-to-One 관계 (한 채팅방에 여러 메시지 가능)
     * 채팅방 삭제 시, 메시지도 함께 삭제됨 (CASCADE 설정)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_chat"))
    private ChatRoom chatRoom;

    /**
     * 메시지를 보낸 사용자 (User)
     * Many-to-One 관계 (한 사용자가 여러 메시지를 보낼 수 있음)
     * 사용자 삭제 시, 메시지 작성자 정보는 NULL로 유지 (SET NULL)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = true, foreignKey = @ForeignKey(name = "fk_message_user"))
    private User sender;

    /**
     * 메시지 내용 (Text)
     * 텍스트 메시지 저장 (최대 2000자 제한)
     */
    @Column(name = "content", length = 2000)
    private String content;

    /**
     * 첨부 파일 데이터 (Binary Large Object - BLOB)
     * 이미지, 문서, 동영상 등의 파일 데이터 저장 가능
     * Null 허용 (파일이 없는 일반 텍스트 메시지도 존재할 수 있음)
     */
    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    /**
     * 메시지 생성 시각 (created_at)
     * Default: 현재 시각
     * 메시지가 생성될 때 자동 저장
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
