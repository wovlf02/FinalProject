package com.hamcam.back.dto.study.team.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hamcam.back.entity.study.team.RoomType;
import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 팀방 생성 요청 DTO
 */
@Getter
@Setter
public class TeamRoomCreateRequest {

    /** 방 제목 */
    private String title;

    /** 방 유형: QUIZ 또는 FOCUS */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private RoomType roomType;

    /** 비밀번호 (선택 입력) */
    private String password;

    /** 공부방인 경우에만 입력 (단위: 분) */
    private int targetTime;

    // ❗️문제풀이방은 나중에 문제 선택 → 문제 관련 필드는 null 허용

    /** 문제 ID (나중에 선택 가능) */
    private Long problemId;

    private String subject;
    private int grade;
    private int month;
    private String difficulty;
}
