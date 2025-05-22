package com.hamcam.back.dto.community.reply.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 대댓글 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyUpdateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotBlank(message = "수정할 내용을 입력해주세요.")
    private String content;
}
