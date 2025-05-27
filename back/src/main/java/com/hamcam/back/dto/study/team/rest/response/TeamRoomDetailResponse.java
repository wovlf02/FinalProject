package com.hamcam.back.dto.study.team.rest.response;

import com.hamcam.back.dto.study.team.response.inner.ParticipantInfo;
import com.hamcam.back.entity.study.team.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * ✅ 팀방 상세 정보 응답
 */
@Getter
@Builder
public class TeamRoomDetailResponse {

    private Long roomId;
    private String title;
    private RoomType roomType;
    private boolean isActive;
    private String inviteCode;

    private List<ParticipantInfo> participants;

    // ✅ QuizRoom
    private String subject;
    private String grade;
    private String month;
    private String difficulty;
    private Long problemId;
    private Boolean isOngoing;

    // ✅ FocusRoom
    private Integer goalMinutes;
    private Boolean isFinished;
    private Long winnerUserId;

    public static TeamRoomDetailResponse of(StudyRoom room, List<StudyRoomParticipant> participantList) {
        List<ParticipantInfo> participants = participantList.stream()
                .map(ParticipantInfo::from)
                .toList();

        RoomType roomType = null;
        if (room instanceof QuizRoom) roomType = RoomType.QUIZ;
        else if (room instanceof FocusRoom) roomType = RoomType.FOCUS;

        TeamRoomDetailResponseBuilder builder = TeamRoomDetailResponse.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomType(roomType)
                .isActive(room.isActive())
                .inviteCode(room.getInviteCode())
                .participants(participants);

        if (room instanceof QuizRoom quiz) {
            builder.subject(quiz.getSubject())
                    .grade(quiz.getGrade())
                    .month(quiz.getMonth())
                    .difficulty(quiz.getDifficulty())
                    .problemId(quiz.getProblemId())
                    .isOngoing(quiz.isOngoing());
        }

        if (room instanceof FocusRoom focus) {
            builder.goalMinutes(focus.getGoalMinutes())
                    .isFinished(focus.isFinished())
                    .winnerUserId(focus.getWinnerUserId());
        }

        return builder.build();
    }
}
