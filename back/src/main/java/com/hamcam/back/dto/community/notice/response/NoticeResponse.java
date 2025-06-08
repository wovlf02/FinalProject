package com.hamcam.back.dto.community.notice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공지사항 응답 DTO (상세/리스트 공통)
 */
@Getter
@Builder
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private int views;
    private LocalDateTime createdAt;
}
