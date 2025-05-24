package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 대댓글 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreateRequest {

    @NotNull(message = "commentId는 필수입니다.")
    private Long commentId;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}
