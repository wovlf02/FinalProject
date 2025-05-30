package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.socket.request.FocusTimeUpdateRequest;
import com.hamcam.back.dto.study.team.socket.request.FocusGoalAchievedRequest;
import com.hamcam.back.dto.study.team.socket.request.FocusConfirmExitRequest;
import com.hamcam.back.dto.study.team.socket.response.FocusRankingResponse;
import com.hamcam.back.service.study.team.socket.FocusRoomSocketService;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FocusRoomSocketController {

    private final FocusRoomSocketService focusRoomSocketService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ✅ 방 입장 시 서버측 처리
     */
    @MessageMapping("/focus/enter")
    public void enterRoom(HttpServletRequest request, Long roomId) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.enterRoom(roomId, userId);
    }

    /**
     * ✅ 클라이언트에서 집중 시간 전송 (1분마다)
     */
    @MessageMapping("/focus/update-time")
    public void updateFocusTime(HttpServletRequest request, FocusTimeUpdateRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.updateFocusTime(requestDto.getRoomId(), userId, requestDto.getFocusedSeconds());

        // 랭킹 계산 후 전송
        FocusRankingResponse ranking = focusRoomSocketService.getCurrentRanking(requestDto.getRoomId());
        messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId(), ranking);
    }

    /**
     * ✅ 목표 시간 달성 시 호출
     */
    @MessageMapping("/focus/goal-achieved")
    public void goalAchieved(HttpServletRequest request, FocusGoalAchievedRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.markGoalAchieved(requestDto.getRoomId(), userId);
    }

    /**
     * ✅ 모든 유저가 결과 확인 버튼 클릭 시
     */
    @MessageMapping("/focus/confirm-exit")
    public void confirmExit(HttpServletRequest request, FocusConfirmExitRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.confirmExit(requestDto.getRoomId(), userId);
    }
}
