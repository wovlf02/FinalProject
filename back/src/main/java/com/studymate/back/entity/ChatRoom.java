package com.studymate.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ChatRoom Entity (채팅방)
 * 1:1 또는 그룹 채팅방을 관리하는 JPA 엔티티
 * chat_members 테이블과 연관 (참여자 정보 저장)
 * chat_messages 테이블과 연관 (채팅 메시지 저장)
 */
@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    /**
     * 채팅방 ID (Primary Key)
     * 자동 증가 (IDENTITY 전략)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * 채팅방 이름 (room_name)
     * 그룹 채팅방의 경우 설정됨 (1:1 채팅은 기본적으로 Null
     * 최대 100자 제한
     */
    @Column(name = "room_name", length = 100)
    private String roomName;

    /**
     * 단체 채팅여부 (is_group)
     * 1:1 채팅 -> 0 / 그룹 채팅 -> 1 로 구분
     * Default: false (1:1 채팅)
     */
    @Column(name = "is_group", nullable = false)
    private boolean isGroup;

    /**
     * 채팅방 생성 시각 (created_at)
     * Default: 현재 시각
     * 채팅방이 생성될 때 자동 저장
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 채팅방이 속한 멤버 목록
     * chat_members 테이블과 연결 (OneToMany 관계)
     * 채팅방 삭제 시 참여자 목록도 함께 삭제 (CASCADE 설정)
     */
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMember> chatMembers;

    /**
     * 채팅방에 포함된 메시지 목록
     * chat_messages 테이블과 연결 (OneToMany 관계)
     * 채팅방 삭제 시 메시지도 함께 삭제 (CASCADE 설정)
     */
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages;
}
