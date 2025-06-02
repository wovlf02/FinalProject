package com.hamcam.back.dto.community.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 신고 처리 결과 단건 응답 DTO
 * <p>
 * 사용자 또는 관리자가 신고를 처리한 후 결과 메시지를 반환할 때 사용됩니다.
 * 일반적으로 성공 메시지, 처리 상태 등을 포함합니다.
 * </p>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    /**
     * 처리 결과 메시지 (예: "신고가 접수되었습니다.")
     */
    private String message;

    /**
     * 처리 성공 여부 (true: 성공, false: 실패)
     */
    private boolean success;

    /**
     * 성공 응답 생성 메서드
     */
    public static ReportResponse success(String message) {
        return ReportResponse.builder()
                .message(message)
                .success(true)
                .build();
    }

    /**
     * 실패 응답 생성 메서드
     */
    public static ReportResponse failure(String message) {
        return ReportResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}
