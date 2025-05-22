package com.hamcam.back.dto.study.team.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [RoomPasswordCheckRequest]
 * 팀 스터디 방 비밀번호 확인 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPasswordCheckRequest {

    @NotNull(message = "방 ID는 필수입니다.")
    private Long roomId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
