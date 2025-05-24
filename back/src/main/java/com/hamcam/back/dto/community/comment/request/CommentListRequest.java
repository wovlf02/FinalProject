package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 댓글 목록 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListRequest {

    @NotNull(message = "postId는 필수입니다.")
    private Long postId;
}
