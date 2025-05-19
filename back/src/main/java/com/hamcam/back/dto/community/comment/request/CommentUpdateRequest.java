package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 댓글 또는 대댓글 수정 요청 DTO
 *
 * <p>
 * 본문 내용(content)을 수정할 수 있으며,
 * 첨부파일은 별도로 처리되며 multipart/form-data와 함께 전달될 수 있습니다.
 * 해당 DTO는 @ModelAttribute 기반 바인딩에 적합합니다.
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequest {

    /**
     * 수정할 본문 내용
     */
    @NotBlank(message = "수정할 내용을 입력해주세요.")
    private String content;
}
