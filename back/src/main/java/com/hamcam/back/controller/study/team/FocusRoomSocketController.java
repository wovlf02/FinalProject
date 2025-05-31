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

    /**
     * ✅ 사용자가 방에 입장할 때 호출됨
     * - 참가자 목록에 등록하고 상태 초기화
     * - 참가자 리스트 브로드캐스트
     */
    @MessageMapping("/focus/enter")
    public void enterRoom(HttpServletRequest request, Long roomId) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.enterRoom(roomId, userId);
        broadcastParticipants(roomId);
    }

    /**
     * ✅ 집중 시간 업데이트 (1분 간격)
     * - 서버에서 누적 집중 시간 저장
     * - 현재 랭킹 계산 후 전체 브로드캐스트
     */
    @MessageMapping("/focus/update-time")
    public void updateFocusTime(HttpServletRequest request, FocusTimeUpdateRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.updateFocusTime(requestDto.getRoomId(), userId, requestDto.getFocusedSeconds());

        FocusRankingResponse ranking = focusRoomSocketService.getCurrentRanking(requestDto.getRoomId());
        messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId(), ranking);
    }

    /**
     * ✅ 목표 시간 도달 처리
     * - 최초 도달자만 승리자로 등록
     * - 전체에 승리자 알림 전송
     */
    @MessageMapping("/focus/goal-achieved")
    public void goalAchieved(HttpServletRequest request, FocusGoalAchievedRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        boolean isFirst = focusRoomSocketService.markGoalAchieved(requestDto.getRoomId(), userId);

        if (isFirst) {
            messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId() + "/winner", userId);
        }
    }

    /**
     * ✅ 결과 확인 처리
     * - 사용자가 "결과 확인" 클릭 시 호출
     * - 전체 확인 여부를 검사하고 방 삭제 여부 판단
     */
    @MessageMapping("/focus/confirm-exit")
    public void confirmExit(HttpServletRequest request, FocusConfirmExitRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.confirmExit(requestDto.getRoomId(), userId);

        broadcastParticipants(requestDto.getRoomId());

        if (focusRoomSocketService.isAllConfirmed(requestDto.getRoomId())) {
            focusRoomSocketService.deleteRoomData(requestDto.getRoomId());
            messagingTemplate.convertAndSend("/sub/focus/room/" + requestDto.getRoomId(), "TERMINATED");
        }
    }

    /**
     * ✅ 방장이 방을 강제로 종료할 때 호출됨
     * - 방장이 아닐 경우 무시됨
     * - 전체 종료 알림 후 참가자 목록도 갱신
     */
    @MessageMapping("/focus/terminate")
    public void terminateRoom(HttpServletRequest request, Long roomId) {
        Long userId = SessionUtil.getUserId(request);
        if (!focusRoomSocketService.isHost(roomId, userId)) return;

        focusRoomSocketService.terminateRoom(roomId);
        messagingTemplate.convertAndSend("/sub/focus/room/" + roomId, "TERMINATED");

        broadcastParticipants(roomId);
    }

    /**
     * ✅ 자리비움/졸음 감지 경고 전송
     * - 클라이언트에서 감지 시 전송
     * - 서버에서는 경고 횟수 누적
     */
    @MessageMapping("/focus/warning")
    public void warning(HttpServletRequest request, FocusWarningRequest requestDto) {
        Long userId = SessionUtil.getUserId(request);
        focusRoomSocketService.accumulateWarning(requestDto.getRoomId(), userId, requestDto.getReason());
    }

    /**
     * ✅ 참가자 목록을 브로드캐스트
     * - 입장/퇴장/확인/종료 시 호출
     */
    private void broadcastParticipants(Long roomId) {
        List<ParticipantInfo> participants = focusRoomSocketService.getCurrentParticipants(roomId);
        messagingTemplate.convertAndSend("/sub/focus/room/" + roomId + "/participants", participants);
    }
}
