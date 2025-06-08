package com.hamcam.back.dto.study.team.socket.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 문제풀이 시작 요청 DTO
 * - 방장만 호출 가능
 * - 문제 조건 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomStartRequest {
    private Long roomId;     // 퀴즈 방 ID
    private Long userId;     // 시작 요청자 ID (방장)
    private String subject;  // 과목 (ex. 국어, 수학, 영어)
    private String unit;     // 단원명 (ex. 지수함수와 로그함수)
    private String level;    // 난이도 (최하, 하, 중, 상, 최상)
}
