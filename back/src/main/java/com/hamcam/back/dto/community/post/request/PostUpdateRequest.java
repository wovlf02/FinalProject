package com.hamcam.back.dto.community.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시글 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotNull(message = "게시글 ID(postId)는 필수입니다.")
    private Long postId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "본문 내용은 필수입니다.")
    private String content;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    private String tag;

    private List<MultipartFile> files;        // 새로 추가할 첨부파일들
    private List<Long> deleteFileIds;         // 기존에서 삭제할 첨부파일 ID 목록
}
