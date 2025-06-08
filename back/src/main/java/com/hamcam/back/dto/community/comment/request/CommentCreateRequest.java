package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 댓글 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequest {

    @NotNull(message = "postId는 필수입니다.")
    private Long postId;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
