package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시글 신고 요청 DTO
 * 사용자가 게시글 신고 시 사용됨
 * 신고 사유와 추가 설명을 포함함
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportRequest {

    /**
     * 신고 사유
     * 사용자가 신고하는 이유
     * 필수 입력 값
     */
    @NotBlank(message = "신고 사유를 입력해주세요")
    private String reason;

    /**
     * 추가 설명 (선택 사항)
     * 신고와 관련된 추가 설명 제공 가능
     */
    private String additionalDetails;
}
