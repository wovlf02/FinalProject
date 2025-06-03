package com.hamcam.back.dto.study.team.rest.response;

import com.hamcam.back.dto.study.team.response.inner.ParticipantInfo;
import com.hamcam.back.entity.study.team.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * ✅ 팀방 상세 정보 응답 DTO
 */
@Getter
@Builder
public class TeamRoomDetailResponse {
    private Long roomId;
    private String title;
    private RoomType roomType;
    private boolean isActive;
    private String inviteCode;
    private String password;
    private int targetTime;                         // FocusRoom만 사용
    private Long problemId;                         // QuizRoom만 사용
    private String subject;
    private int grade;
    private int month;
    private String difficulty;
    private List<ParticipantInfo> participants;     // 참가자 목록
}
