package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 대댓글 응답 DTO
 * 사용자가 대댓글을 조회할 때 사용됨
 */
@Getter
@Setter
@AllArgsConstructor
public class ReplyResponse {

    /**
     * 대댓글 ID
     * 데이터베이스에서 자동 생성됨
     */
    private Long id;

    /**
     * 부모 댓글 ID
     * 대댓글이 속한 상위 댓글 ID
     */
    private Long parentCommentId;

    /**
     * 작성자 ID
     * 대댓글을 작성한 사용자의 ID
     */
    private Long userId;

    /**
     * 작성자 이름
     * 대댓글을 작성한 사용자의 이름 또는 닉네임
     */
    private String author;

    /**
     * 대댓글 내용
     * 사용자가 입력한 내용
     */
    private String content;

    /**
     * 작성 시각
     * 대댓글이 처음 작성된 날짜 및 시각
     */
    private LocalDateTime createdAt;

    /**
     * 수정 시각
     * 대댓글이 마지막으로 수정된 날짜 및 시각
     */
    private LocalDateTime updatedAt;
}
