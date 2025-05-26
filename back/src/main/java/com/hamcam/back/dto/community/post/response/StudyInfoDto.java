package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커뮤니티 게시판 사이드바 스터디 요약 정보 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyInfoDto {

    private String name;      // 스터디 이름
    private String color;     // 배경 색상 코드
    private String tag;       // 상태 태그 (예: 모집중, 마감)
    private String tagColor;  // 태그 색상 (ex: #10b981)
    private String info;      // 활동 정보 (예: 매주 수요일 14시 | 10명 활동)
}
