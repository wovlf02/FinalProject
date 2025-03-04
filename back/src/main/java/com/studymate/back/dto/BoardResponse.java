package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 응답 DTO
 * 게시글 상세 조회 및 목록 조회 시 사용됨
 * 게시글의 메타 정보와 파일 URL을 포함함
 */
@Getter
@Setter
@AllArgsConstructor
public class BoardResponse {

    /**
     * 게시글 ID
     * 데이터베이스에서 자동 생성됨
     */
    private Long id;

    /**
     * 게시글 제목
     * 사용자가 입력한 제목
     */
    private String title;

    /**
     * 게시글 내용
     * 사용자가 입력한 내용
     */
    private String content;

    /**
     * 작성자 정보
     * 게시글을 작성한 사용자의 이름 또는 닉네임
     */
    private String author;

    /**
     * 게시글 추천 수
     * 사용자가 게시글을 추천하면 증가
     */
    private int likeCount;

    /**
     * 첨부파일 URL 목록 (선택 사항)
     * 게시글에 첨부된 파일들의 URL 목록
     */
    private List<String> attachmentUrls;

    /**
     * 게시글 생성 시각
     * 게시글이 처음 생성된 날짜 및 시각
     */
    private LocalDateTime createdAt;

    /**
     * 게시글 수정 시각
     * 게시글이 마지막으로 수정된 날짜 및 시각
     */
    private LocalDateTime updatedAt;
}
