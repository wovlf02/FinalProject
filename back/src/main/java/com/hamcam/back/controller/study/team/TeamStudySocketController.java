package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.socket.*;
import com.hamcam.back.service.study.team.TeamStudySocketService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamStudySocketController {

    private final TeamStudySocketService socketService;

    @Resource
    private final SimpMessagingTemplate messagingTemplate;

    /** ✅ 채팅 메시지 전송 */
    @MessageMapping("/study/team/chat")
    public void sendChat(@Payload TeamChatMessage message) {
        socketService.saveChatMessage(message);
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + message.getRoomId() + "/chat",
                message
        );
    }

    /** ✅ 손들기 요청 */
    @MessageMapping("/study/team/raise")
    public void raiseHand(@Payload TeamRaiseRequest request) {
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + request.getRoomId() + "/raise",
                request.getUserId()
        );
    }

    /** ✅ 발표 시작 */
    @MessageMapping("/study/team/present/start")
    public void startPresentation(@Payload PresentationStartRequest request) {
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + request.getRoomId() + "/presentation/start",
                request
        );
    }

    /** ✅ 발표 종료 후 투표 시작 */
    @MessageMapping("/study/team/present/end")
    public void endPresentation(@Payload PresentationEndRequest request) {
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + request.getRoomId() + "/vote/start",
                request
        );
    }

    /** ✅ 투표 수신 처리 및 결과 브로드캐스트 */
    @MessageMapping("/study/team/vote")
    public void handleVote(@Payload VoteMessage vote) {
        boolean success = socketService.collectVote(vote);
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + vote.getRoomId() + "/vote/result",
                success ? "SUCCESS" : "FAIL"
        );
    }

    /** ✅ 집중시간 업데이트 (FocusRoom용) */
    @MessageMapping("/study/team/focus/update")
    public void updateFocus(@Payload FocusTimeUpdate update) {
        socketService.updateFocusTime(update);
        List<FocusRankResponse> ranks = socketService.getCurrentRanking(update.getRoomId());
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + update.getRoomId() + "/rank",
                ranks
        );
    }
}
