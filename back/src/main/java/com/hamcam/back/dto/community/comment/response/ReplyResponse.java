package com.hamcam.back.dto.community.comment.response;

import com.hamcam.back.entity.community.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 대댓글 응답 DTO
 *
 * 게시글 댓글의 대댓글 데이터를 클라이언트에 전달하는 용도입니다.
 */
@Getter
@Builder
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
     * 내가 좋아요를 눌렀는지 여부
     */
    private boolean liked;

    /**
     * 엔티티 → DTO 변환 메서드
     *
     * @param reply Reply 엔티티
     * @param currentUserId 현재 사용자 ID
     * @return 변환된 ReplyResponse
     */
    public static ReplyResponse from(Reply reply, Long currentUserId) {
        boolean liked = reply.getLikes() != null &&
                reply.getLikes().stream()
                        .anyMatch(like -> like.getUser().getId().equals(currentUserId));

        return ReplyResponse.builder()
                .replyId(reply.getId())
                .writerId(reply.getWriter().getId())
                .writerNickname(reply.getWriter().getNickname())
                .profileImageUrl(reply.getWriter().getProfileImageUrl())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .likeCount(reply.getLikes() != null ? reply.getLikes().size() : 0)
                .liked(liked)
                .build();
    }
}
