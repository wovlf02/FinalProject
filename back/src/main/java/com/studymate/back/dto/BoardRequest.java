package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 게시글 요청 DTO
 * 게시글 작성 및 수정 시 사용됨
 * 제목, 내용, 첨부파일 URL을 포함함
 */
@Getter
@Setter
@NoArgsConstructor
public class BoardRequest {

    /**
     * 게시글 제목
     * 사용자가 입력해야 하는 필수 값
     * 최대 100자까지 입력 가능
     */
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;

    /**
     * 게시글 내용
     * 사용자가 입력해야 하는 필수 값
     */
    @NotBlank(message = "내용을 입력해야 합니다.")
    private String content;

    /**
     * 첨부파일 URL 목록 (선택 사항)
     * 사용자가 업로드한 파일의 URL 리스트
     */
    private List<String> attachmentUrls;
}
