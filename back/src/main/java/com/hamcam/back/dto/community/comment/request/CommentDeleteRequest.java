package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 댓글 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeleteRequest {

    @NotNull(message = "commentId는 필수입니다.")
    private Long commentId;
}
