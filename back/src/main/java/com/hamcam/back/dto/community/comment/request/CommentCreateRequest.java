package com.hamcam.back.dto.community.comment.request;

import lombok.Data;

/**
 * 댓글 생성 요청 DTO
 * - 댓글 본문만 포함하며, 작성자는 인증된 사용자로 서버에서 처리합니다.
 * - 첨부파일은 MultipartFile[]로 별도 전달됩니다.
 */
@Data
public class CommentCreateRequest {

    /**
     * 댓글 본문
     */
    private String content;
}
