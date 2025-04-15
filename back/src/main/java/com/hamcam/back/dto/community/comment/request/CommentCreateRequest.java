package com.hamcam.back.dto.community.comment.request;

import lombok.Data;

/**
 * 댓글 또는 대댓글 생성 요청 DTO
 * <p>
 * 본문 텍스트 및 작성자 ID 정보를 포함합니다.
 * 첨부파일은 MultipartFile[] 형태로 별도 전달되며,
 * 이 DTO는 @ModelAttribute 기반으로 처리됩니다.
 * </p>
 */
@Data
public class CommentCreateRequest {

    /**
     * 댓글 또는 대댓글의 본문 내용
     */
    private String content;

    /**
     * 작성자 사용자 ID
     */
    private Long writerId;
}
