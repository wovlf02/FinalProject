package com.hamcam.back.dto.study.team.rest.request;

import com.hamcam.back.entity.study.team.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 팀방 생성 요청 DTO
 * - QuizRoom 또는 FocusRoom 공통 처리
 */
@Getter
@Setter
@NoArgsConstructor
public class TeamRoomCreateRequest {
    private String title;
    private String password; // optional
    private RoomType roomType; // QUIZ or FOCUS

    // ✅ QuizRoom 필드
    private String subject;
    private String grade;
    private String month;
    private String difficulty;
    private Long problemId;

    // ✅ FocusRoom 필드
    private int goalMinutes;
}
