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

    /**
     * 삭제 요청 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
