package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 채팅방 생성 요청 DTO
 * 1:1 또는 단체 채팅방 생성 시 사용됨
 * 채팅방의 이름과 참가자 목록을 포함함
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomRequest {

    /**
     * 채팅방 이름
     * 단체 채팅방일 경우 필수 입력
     */
    @NotBlank(message = "채팅방 이름을 입력해주세요.")
    private String roomName;

    /**
     * 참가자 ID 목록
     * 1:1 채팅의 경우 하나의 ID만 포함됨
     * 단체 채팅의 경우 여러 명 추가 가능
     */
    @NotNull(message = "최소 한 명 이상의 참가자를 추가해주세요.")
    private List<Long> participantIds;
}
