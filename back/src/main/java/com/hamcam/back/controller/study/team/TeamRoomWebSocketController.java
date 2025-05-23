package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.study.team.websocket.TeamRoomStompMessage;
import com.hamcam.back.service.study.team.TeamRoomWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * [TeamRoomWebSocketController]
 * 문제풀이방 / 공부시간 경쟁방의 실시간 STOMP 메시지를 처리하는 WebSocket 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class TeamRoomWebSocketController {

    private final TeamRoomWebSocketService teamRoomWebSocketService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 🙋 참가자 손들기 요청
     */
    @MessageMapping("/teamroom/raise-hand")
    public void handleRaiseHand(TeamRoomStompMessage message) {
        log.info("🙋‍♂️ 손들기 요청 - userId: {}, roomId: {}", message.getUserId(), message.getRoomId());
        teamRoomWebSocketService.handleRaiseHand(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/raise-hand", message);
    }

    /**
     * 🗣 발표자 지정 (방장)
     */
    @MessageMapping("/teamroom/set-presenter")
    public void setPresenter(TeamRoomStompMessage message) {
        log.info("🗣 발표자 지정 - userId: {}, roomId: {}", message.getUserId(), message.getRoomId());
        teamRoomWebSocketService.setPresenter(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/presenter", message);
    }

    /**
     * 🗳 투표 시작
     */
    @MessageMapping("/teamroom/vote/start")
    public void startVoting(TeamRoomStompMessage message) {
        log.info("🗳 투표 시작 - roomId: {}", message.getRoomId());
        teamRoomWebSocketService.startVoting(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/vote/start", message);
    }

    /**
     * ✅ 투표 응답
     */
    @MessageMapping("/teamroom/vote/respond")
    public void respondVote(TeamRoomStompMessage message) {
        log.info("✅ 투표 응답 - userId: {}, result: {}", message.getUserId(), message.getVoteResult());
        teamRoomWebSocketService.processVoteResponse(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/vote/result", message);
    }

    /**
     * 📊 실시간 랭킹 갱신
     */
    @MessageMapping("/teamroom/ranking/update")
    public void updateRanking(TeamRoomStompMessage message) {
        log.info("📊 랭킹 업데이트 요청 - roomId: {}", message.getRoomId());
        teamRoomWebSocketService.broadcastRanking(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/ranking", message);
    }

    /**
     * 🚀 문제풀이 시작 브로드캐스트
     */
    @MessageMapping("/teamroom/quiz/start")
    public void startQuiz(TeamRoomStompMessage message) {
        log.info("🚀 문제풀이 시작 - roomId: {}", message.getRoomId());
        teamRoomWebSocketService.startQuiz(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/quiz/start",
                MessageResponse.of("문제풀이가 시작되었습니다."));
    }

    /**
     * 🛑 문제풀이 종료 브로드캐스트
     */
    @MessageMapping("/teamroom/quiz/terminate")
    public void terminateQuiz(TeamRoomStompMessage message) {
        log.info("🛑 문제풀이 종료 - roomId: {}", message.getRoomId());
        teamRoomWebSocketService.terminateQuiz(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/quiz/terminate",
                MessageResponse.of("문제풀이가 종료되었습니다."));
    }

    /**
     * 📤 실패 문제 업로드 알림
     */
    @MessageMapping("/teamroom/quiz/upload")
    public void uploadUnsolvedQuestion(TeamRoomStompMessage message) {
        log.info("📤 실패 문제 커뮤니티 업로드 - roomId: {}", message.getRoomId());
        teamRoomWebSocketService.uploadUnsolvedQuestion(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/quiz/upload",
                MessageResponse.of("문제가 커뮤니티에 업로드되었습니다."));
    }

    /**
     * 🎯 Focus 경쟁 종료 브로드캐스트
     */
    @MessageMapping("/teamroom/focus/complete")
    public void completeFocusSession(TeamRoomStompMessage message) {
        log.info("🎯 Focus 경쟁방 종료 - roomId: {}", message.getRoomId());
        teamRoomWebSocketService.completeFocus(message);
        messagingTemplate.convertAndSend("/sub/teamroom/" + message.getRoomId() + "/focus/complete",
                MessageResponse.of("공부시간 경쟁이 종료되었습니다."));
    }
}
