package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 대댓글 요청 DTO
 * 사용자가 대댓글을 작성/수정할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class ReplyRequest {

    /**
     * 부모 댓글 ID
     * 대댓글을 작성할 상위 댓글의 ID
     */
    @NotNull(message = "대댓글을 작성할 부모 댓글 ID가 필요합니다.")
    private Long parentCommentId;

    /**
     * 작성자 ID
     * 대댓글을 작성하는 사용자의 ID
     */
    @NotNull(message = "작성자 ID가 필요합니다.")
    private Long userId;

    /**
     * 대댓글 내용
     * 사용자가 입력해야 하는 필수 값
     */
    @NotBlank(message = "내용을 입력해야 합니다.")
    private String content;
}
