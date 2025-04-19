package com.hamcam.back.dto.community.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 신고 처리 결과 단건 응답 DTO
 * <p>
 * 사용자 또는 관리자가 신고를 처리한 후 결과 메시지를 반환할 때 사용됩니다.
 * 일반적으로 성공 메시지, 처리 상태 등을 포함합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class ReportResponse {

    /**
     * 처리 결과 메시지
     */
    private String message;

    /**
     * 성공 여부
     */
    private boolean success;
}
