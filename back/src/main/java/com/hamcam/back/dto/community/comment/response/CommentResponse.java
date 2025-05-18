package com.hamcam.back.dto.community.comment.response;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 응답 DTO
 *
 * 댓글 정보와 대댓글 리스트를 포함한 구조입니다.
 * 프론트에서는 이를 기반으로 계층형 UI를 렌더링할 수 있습니다.
 */
@Getter
@AllArgsConstructor
@Builder
public class CommentResponse {

    /**
     * 댓글 ID
     */
    private Long commentId;

    /**
     * 댓글 작성자 ID
     */
    private Long writerId;

    /**
     * 댓글 작성자 닉네임
     */
    private String writerNickname;

    /**
     * 작성자 프로필 이미지 URL
     */
    private String profileImageUrl;

    /**
     * 댓글 본문
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
     * 현재 로그인 사용자의 좋아요 여부
     */
    private boolean liked;

    /**
     * 대댓글 리스트
     */
    private List<ReplyResponse> replies;

    /**
     * 댓글 엔티티 → DTO 변환 메서드
     *
     * @param comment 댓글 엔티티
     * @param replies 해당 댓글에 연결된 대댓글 목록
     * @param currentUserId 현재 로그인 사용자 ID (좋아요 여부 확인용)
     * @return 변환된 CommentResponse 객체
     */
    public static CommentResponse from(Comment comment, List<Reply> replies, Long currentUserId) {
        boolean liked = comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(currentUserId));

        return CommentResponse.builder()
                .commentId(comment.getId())
                .writerId(comment.getWriter().getId())
                .writerNickname(comment.getWriter().getNickname())
                .profileImageUrl(comment.getWriter().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeCount(comment.getLikes().size())
                .liked(liked)
                .replies(replies.stream()
                        .map(reply -> ReplyResponse.from(reply, currentUserId))
                        .collect(Collectors.toList()))
                .build();
    }
}
