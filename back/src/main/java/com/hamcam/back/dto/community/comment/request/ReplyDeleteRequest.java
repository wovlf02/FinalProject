package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 대댓글 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDeleteRequest {

    @NotNull(message = "replyId는 필수입니다.")
    private Long replyId;
}
