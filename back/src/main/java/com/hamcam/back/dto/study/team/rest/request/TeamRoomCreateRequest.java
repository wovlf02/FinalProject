package com.hamcam.back.dto.study.team.rest.request;

import com.hamcam.back.entity.study.team.RoomType;
import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 팀방 생성 요청 DTO
 */
@Getter
@Setter
public class TeamRoomCreateRequest {

    private String title;               // 방 제목
    private RoomType roomType;          // QUIZ 또는 FOCUS
    private String password;            // 선택 입력
    private int targetTime;             // FocusRoom의 경우 (단위: 분)

    // QuizRoom의 경우 문제 필터링/선택 관련 필드
    private Long problemId;             // 직접 선택한 문제 ID
    private String subject;             // 과목
    private int grade;                  // 학년
    private int month;                  // 월
    private String difficulty;          // 난이도
}
