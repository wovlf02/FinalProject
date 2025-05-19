package com.hamcam.back.dto.community.comment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 댓글 생성 요청 DTO
 *
 * - 댓글 본문(content)만 포함되며, 작성자 정보는 서버에서 인증된 사용자 기준으로 처리됩니다.
 * - 첨부파일은 multipart/form-data로 별도 전달됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequest {

    /**
     * 댓글 본문
     */
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
