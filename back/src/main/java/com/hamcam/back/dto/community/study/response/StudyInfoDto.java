package com.hamcam.back.dto.community.study.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyInfoDto {
    private Long studyId;
    private Long creatorId;
    private String name;
    private String info;
    private String schedule;
    private String status;
    private String tag;
    private String color;
    private String tagColor;
    private int members;                        // 총 모집 인원 수
    private int currentMembers;                // 현재 참여자 수
    private List<UserSimpleDto> participants;  // ✅ 현재 참여자 정보 리스트
}
