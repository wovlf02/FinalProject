package com.hamcam.back.dto.community.comment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 대댓글 응답 DTO
 */
@Data
@AllArgsConstructor
public class ReplyResponse {

    /**
     * 대댓글 ID
     */
    private Long replyId;

    /**
     * 작성자 ID
     */
    private Long writerId;

    /**
     * 작성자 닉네임
     */
    private String writerNickname;

    /**
     * 프로필 이미지 URL
     */
    private String profileImageUrl;

    /**
     * 대댓글 내용
     */
    private String content;

    /**
     * 작성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 수정 시각
     */
    private LocalDateTime updatedAt;

    /**
     * 좋아요 수
     */
    private int likeCount;

    /**
     * 내가 좋아요 눌렀는지 여부
     */
    private boolean liked;
}
