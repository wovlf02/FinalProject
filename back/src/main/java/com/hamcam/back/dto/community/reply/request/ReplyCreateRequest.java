package com.hamcam.back.dto.community.reply.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대댓글 생성 요청 DTO
 * <p>
 * 대댓글 본문 내용만 포함하며, 작성자는 서버에서 인증된 사용자로 처리됩니다.
 * 첨부파일은 별도로 MultipartFile[] 형태로 전달됩니다.
 * 이 DTO는 @ModelAttribute 또는 @RequestBody 방식으로 매핑됩니다.
 * </p>
 */
@Data
@NoArgsConstructor
public class ReplyCreateRequest {

    /**
     * 대댓글 본문 내용
     */
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}
