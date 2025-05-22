package com.hamcam.back.dto.community.comment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
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

    private Long commentId;
    private Long writerId;
    private String writerNickname;
    private String profileImageUrl;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private int likeCount;
    private boolean liked;

    @Builder.Default
    private List<ReplyResponse> replies = Collections.emptyList();

    public static CommentResponse from(Comment comment, List<Reply> replies, Long currentUserId) {
        boolean liked = comment.getLikes() != null &&
                comment.getLikes().stream()
                        .anyMatch(like -> like.getUser().getId().equals(currentUserId));

        int likeCount = comment.getLikes() != null ? comment.getLikes().size() : 0;

        return CommentResponse.builder()
                .commentId(comment.getId())
                .writerId(comment.getWriter().getId())
                .writerNickname(comment.getWriter().getNickname())
                .profileImageUrl(comment.getWriter().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeCount(likeCount)
                .liked(liked)
                .replies(replies != null
                        ? replies.stream()
                        .map(reply -> ReplyResponse.from(reply, currentUserId))
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}
