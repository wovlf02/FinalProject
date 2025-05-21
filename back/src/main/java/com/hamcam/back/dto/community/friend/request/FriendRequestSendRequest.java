package com.hamcam.back.dto.community.friend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [FriendRequestSendRequest]
 *
 * 친구 요청 전송 요청 DTO입니다.
 * 요청 대상 사용자의 닉네임을 포함하며,
 * 해당 닉네임은 서버에서 유효 사용자 검증에 사용됩니다.
 *
 * 예시 요청:
 * {
 *   "nickname": "테스트2",
 *   "message": "같이 공부해요!"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestSendRequest {

    @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
    @JsonProperty("targetUserId")
    private Long targetUserId;
}
