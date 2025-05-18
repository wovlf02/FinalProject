package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 과목 목록 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdateSubjectsRequest {

    @NotEmpty(message = "과목은 최소 1개 이상 선택해야 합니다.")
    private List<String> subjects;
}
