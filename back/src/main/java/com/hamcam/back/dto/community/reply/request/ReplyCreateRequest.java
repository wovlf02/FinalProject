package com.hamcam.back.dto.community.reply.request;

import lombok.Data;

/**
 * 대댓글 생성 요청 DTO
 * <p>
 * 대댓글 본문 텍스트만 포함하며, 작성자는 인증된 사용자로 서버에서 처리합니다.
 * 첨부파일은 MultipartFile[]로 별도 전달됩니다.
 * 이 DTO는 @ModelAttribute 기반으로 처리됩니다.
 * </p>
 */
@Data
public class ReplyCreateRequest {

    /**
     * 대댓글 내용
     */
    private String content;
}
