package com.hamcam.back.dto.community.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 커뮤니티 사이드바용 스터디 리스트 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyInfoListResponse {

    private List<StudyInfoDto> studies;
}
