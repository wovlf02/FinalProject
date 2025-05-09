package com.hamcam.back.dto.community.comment.response;

import com.hamcam.back.entity.community.Comment;
import com.hamcam.back.entity.community.Reply;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 응답 DTO
 * <p>
 * 댓글 단건 정보와 해당 댓글의 대댓글 리스트를 포함합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private Long writerId;
    private String writerNickname;
    private String profileImageUrl;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private boolean liked;
    private List<ReplyResponse> replies;

    /**
     * Comment → CommentResponse 변환 정적 메서드
     *
     * @param comment 댓글 엔티티
     * @param replies 대댓글 리스트
     * @return 댓글 응답 DTO
     */
    public static CommentResponse from(Comment comment, List<Reply> replies) {
        return new CommentResponse(
                comment.getId(),
                comment.getWriter().getId(),
                comment.getWriter().getUsername(), // 닉네임으로 따로 필드 있을 경우 수정
                comment.getWriter().getProfileImageUrl(), // User 엔티티에 해당 필드 필요
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getLikes().size(), // Like 리스트 필요
                false, // 임시 liked 값 (SecurityContextHolder 도입 시 수정)
                replies.stream()
                        .map(ReplyResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
