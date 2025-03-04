package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 응답 DTO
 * 채팅방 목록 조회 및 상세 정보 조회 시 사용됨
 */
@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponse {

    /**
     * 채팅방 ID
     * 데이터베이스에서 자동 생성됨
     */
    private Long id;

    /**
     * 채팅방 이름
     * 단체 채팅방일 경우 이름이 설정됨
     */
    private String roomName;

    /**
     * 참가자 목록 (ID 리스트)
     * 채팅방에 참여 중인 사용자 목록
     */
    private List<Long> participantIds;

    /**
     * 생성 시간
     * 채팅방이 생성된 날짜 및 시간
     */
    private LocalDateTime createdAt;
}
