package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅방 비밀번호 보호 요청 DTO
 * 사용자가 특정 채팅방을 비밀번호 보호 모드로 설정할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomLockRequest {

    /**
     * 비밀번호 설정
     * 채팅방을 보호할 비밀번호
     */
    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    private String password;
}
