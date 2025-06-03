package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.socket.request.*;
import com.hamcam.back.dto.study.team.socket.response.FocusRankingResponse;
import com.hamcam.back.dto.study.team.socket.response.ParticipantInfo;
import com.hamcam.back.service.study.team.socket.FocusRoomSocketService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FocusRoomSocketController {

    private final FocusRoomSocketService focusRoomSocketService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TERMINATED = "TERMINATED";

    /**
     * ✅ 방 입장
     */
    @MessageMapping("/focus/enter")
    public void enterRoom(HttpServletRequest request, Long roomId) {
        Long userId = extractUserId(request);
        log.info("🎥 [입장] userId={} roomId={}", userId, roomId);

        focusRoomSocketService.enterRoom(roomId, userId);
        broadcastParticipants(roomId);
    }

    /**
     * ✅ 집중 시간 업데이트
     */
    @MessageMapping("/focus/update-time")
    public void updateFocusTime(HttpServletRequest request, FocusTimeUpdateRequest requestDto) {
        Long userId = extractUserId(request);
        focusRoomSocketService.updateFocusTime(requestDto.getRoomId(), userId, requestDto.getFocusedSeconds());

        FocusRankingResponse ranking = focusRoomSocketService.getCurrentRanking(requestDto.getRoomId());
        messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId(), ranking);
    }

    /**
     * ✅ 목표 시간 도달
     */
    @MessageMapping("/focus/goal-achieved")
    public void goalAchieved(HttpServletRequest request, FocusGoalAchievedRequest requestDto) {
        Long userId = extractUserId(request);
        boolean isFirst = focusRoomSocketService.markGoalAchieved(requestDto.getRoomId(), userId);

        if (isFirst) {
            messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId() + "/winner", userId);
        }
    }

    /**
     * ✅ 결과 확인 및 방 종료
     */
    @MessageMapping("/focus/confirm-exit")
    public void confirmExit(HttpServletRequest request, FocusConfirmExitRequest requestDto) {
        Long userId = extractUserId(request);
        focusRoomSocketService.confirmExit(requestDto.getRoomId(), userId);
        broadcastParticipants(requestDto.getRoomId());

        if (focusRoomSocketService.isAllConfirmed(requestDto.getRoomId())) {
            focusRoomSocketService.deleteRoomData(requestDto.getRoomId());
            messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId(), TERMINATED);
        }
    }

    /**
     * ✅ 방 강제 종료
     */
    @MessageMapping("/focus/terminate")
    public void terminateRoom(HttpServletRequest request, Long roomId) {
        Long userId = extractUserId(request);
        if (!focusRoomSocketService.isHost(roomId, userId)) return;

        focusRoomSocketService.terminateRoom(roomId);
        messagingTemplate.convertAndSend("/sub/focus/room/" + roomId, TERMINATED);
        broadcastParticipants(roomId);
    }

    /**
     * ✅ 경고 감지
     */
    @MessageMapping("/focus/warning")
    public void warning(HttpServletRequest request, FocusWarningRequest requestDto) {
        Long userId = extractUserId(request);
        focusRoomSocketService.accumulateWarning(requestDto.getRoomId(), userId, requestDto.getReason());
    }

    /**
     * ✅ 참가자 목록 전송
     */
    private void broadcastParticipants(Long roomId) {
        List<ParticipantInfo> participants = focusRoomSocketService.getCurrentParticipants(roomId);
        messagingTemplate.convertAndSend("/sub/focus/room/" + roomId + "/participants", participants);
    }

    /**
     * ✅ 세션에서 userId 추출 공통화
     */
    private Long extractUserId(HttpServletRequest request) {
        return SessionUtil.getUserId(request);
    }
}
