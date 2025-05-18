package com.hamcam.back.dto.study;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 팀 스터디방 생성 요청 DTO
 * <p>
 * QUIZ 또는 FOCUS 유형의 방을 생성할 때 클라이언트로부터 전달되는 요청 형식입니다.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomCreateRequest {

    /**
     * 방 제목 (예: 수학 집중방, 영어 문제풀이방 등)
     */
    @NotBlank(message = "방 제목은 필수 입력값입니다.")
    private String title;

    /**
     * 방 유형 (QUIZ 또는 FOCUS)
     */
    @NotBlank(message = "방 유형은 필수 입력값입니다.")
    private String roomType;

    /**
     * 최대 참여 인원 (2~20명)
     */
    @NotNull(message = "최대 인원은 필수 입력값입니다.")
    @Min(value = 2, message = "최소 인원은 2명입니다.")
    @Max(value = 20, message = "최대 인원은 20명입니다.")
    private Integer maxParticipants;

    /**
     * 비밀번호 (비공개 방일 경우 선택적으로 설정)
     */
    private String password;
}
