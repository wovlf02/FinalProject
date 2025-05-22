package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 댓글/대댓글 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequest {

    /**
     * 수정 요청 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 수정할 본문 내용
     */
    @NotBlank(message = "수정할 내용을 입력해주세요.")
    private String content;
}
