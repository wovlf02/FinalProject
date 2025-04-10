package com.hamcam.back.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * [ChatRoom 엔티티 클래스]
 * 
 * 채팅 기능의 핵심인 채팅방 정보를 저장하는 엩니니 
 * 채팅방은 일반 게시글 기반 쪽지, 그룹 스터디 채팅방, 그룹 내 실시간 채팅 등으로 분류되며,
 * room_type과 reference_id 필드를 통해 그 목적과 연동 정보를 파악할 수 있음
 * 
 * 이 엔티티는 WebSocket 기반 채팅에서 사용되며,
 * 채팅방 목록 조회, 채팅방 생성, 입장, 퇴장 등 다양한 기능과 연계됨
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
     * [PK] 채팅방 고유 ID
     * 각 채팅방을 고유하게 식별할 수 있도록 자동 증가하는 정수형 ID 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    /**
     * 채팅방의 유형 구분
     *
     * POST: 게시글 기반 쪽지형 채팅방 (1:1 혹은 제한적 다자)
     * STUDY: 캠스터디 기반 실시간 채팅방
     * GROUP: 그룹 내 자유 채팅방
     *
     * 해당 타입에 따라 채팅방 내 기능 및 UI, 알림 방식 등이 달라질 수 있음
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType = RoomType.POST;

    /**
     * 외부 참조 ID (reference_id)
     *
     * 특정 게시글(post), 스터디 세션(study), 그룹(group)과 연결되는 연동 ID
     *
     * ex)
     * -> 게시글 기반이면 게시글의 post_id
     * -> 그룹이면 그룹의 team_id
     * -> 해당 기능과의 연계 추적을 위해 사용
     *
     * nullable 처리하여 자유롭게 확장 가능
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * 채팅방 생성 시각
     * 
     * 채팅방이 처음 생성된 날짜 및 시간 기록
     * 자동 생성 (INSERT 시점에 CURRENT_TIMESTAMP로 입력)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * [ENUM] 채팅방 유형
     * 
     * 프론트엔드에서 채팅방 리스트 또는 상세 페이지를 분류하기 위해 사용됨
     * 기능/뷰/알림 방식 등을 동적으로 처리할 수 있음
     */
    public enum RoomType {
        POST, STUDY, GROUP
    }
}
