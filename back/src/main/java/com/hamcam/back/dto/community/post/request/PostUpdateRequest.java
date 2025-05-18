package com.hamcam.back.dto.community.post.request;

import com.hamcam.back.entity.community.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [PostUpdateRequest]
 *
 * 게시글 수정 요청 DTO입니다.
 * 제목, 본문, 카테고리, 첨부파일 삭제 목록 등을 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    /**
     * 수정할 제목 (null 또는 빈 문자열이면 변경 없음)
     */
    private String title;

    /**
     * 수정할 본문 내용 (null 또는 빈 문자열이면 변경 없음)
     */
    private String content;

    /**
     * 수정할 카테고리 (예: QUESTION, INFO, STUDY, ANONYMOUS)
     * null이면 변경 없음
     */
    private PostCategory category;

    /**
     * 삭제할 첨부파일 ID 목록 (선택적)
     * 기존에 업로드한 첨부파일 중 삭제할 파일 ID 리스트
     */
    private List<Long> deleteFileIds;
}
