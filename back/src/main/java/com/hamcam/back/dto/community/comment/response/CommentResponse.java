package com.hamcam.back.dto.community.comment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 응답 DTO
 * <p>
 * 댓글 단건 정보와 해당 댓글의 대댓글 리스트를 포함합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class CommentResponse {

    /**
     * 댓글 ID
     */
    private Long commentId;

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
     * 댓글 내용
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

    /**
     * 대댓글 리스트
     */
    private List<ReplyResponse> replies;
}
