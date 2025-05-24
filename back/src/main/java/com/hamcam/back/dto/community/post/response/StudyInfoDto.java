package com.hamcam.back.dto.community.post.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyInfoDto {
    private String name;      // 스터디 이름
    private String color;     // 배경 색상 코드
    private String tag;       // 상태 태그 (예: 모집중)
    private String tagColor;  // 태그 색상
    private String info;      // 활동 정보 (예: 매주 수요일 | 10명 활동)
}
