package com.hamcam.back.dto.study.team.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * [QuizRoomCreateRequest]
 * 문제풀이방(QUIZ) 생성 시 사용하는 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRoomCreateRequest {

    @NotNull(message = "생성자 ID는 필수입니다.")
    private Long userId;

    @NotBlank(message = "방 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "과목을 선택해주세요.")
    private String subject;

    @NotNull(message = "학년은 필수입니다.")
    private Integer grade;

    @NotNull(message = "월은 필수입니다.")
    private Integer month;

    @NotBlank(message = "난이도를 선택해주세요.")
    private String difficulty;

    @NotNull(message = "선택한 문제 ID는 필수입니다.")
    private Long problemId;

    private String password;

    @Min(value = 3, message = "최소 인원은 3명 이상이어야 합니다.")
    @Max(value = 20, message = "최대 인원은 20명입니다.")
    private int maxParticipants;
}
