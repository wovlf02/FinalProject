package com.hamcam.back.dto.community.comment.request;

import lombok.Data;

/**
 * 댓글 또는 대댓글 수정 요청 DTO
 * <p>
 * 본문과 첨부파일을 수정할 수 있으며,
 * 기존 파일 삭제는 서비스 로직에서 따로 처리해야 할 수 있습니다.
 * 이 요청도 multipart/form-data로 전달되며,
 * @ModelAttribute로 매핑됩니다.
 * </p>
 */
@Data
public class CommentUpdateRequest {

    /**
     * 수정할 본문 내용
     */
    private String content;

}
