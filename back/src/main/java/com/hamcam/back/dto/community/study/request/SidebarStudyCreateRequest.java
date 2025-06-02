package com.hamcam.back.dto.community.study.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SidebarStudyCreateRequest {
    private String name;       // 스터디 이름
    private String info;       // 스터디 소개
    private String schedule;   // 스터디 일정
    private String status;     // 모집중 / 마감
    private String tag;        // 태그
    private String color;      // 배경색
    private String tagColor;   // 태그색
    private int members;       // 모집 인원
}
