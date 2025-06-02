package com.hamcam.back.dto.community.comment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hamcam.back.entity.community.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 대댓글 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class ReplyResponse {

    private Long replyId;
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
