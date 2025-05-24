package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 게시글 생성 및 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class PostSaveRequest {

    private Long postId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private PostCategory category;

    private List<MultipartFile> files;        // 업로드 파일
    private List<Long> deleteFileIds;         // 삭제할 첨부파일 ID
}

