package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.socket.*;
import com.hamcam.back.service.study.team.TeamStudySocketService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TeamStudySocketController {

    private final TeamStudySocketService socketService;

    @Resource
    private final SimpMessagingTemplate messagingTemplate;

    /** ✅ 실시간 채팅 */
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
    public void handleRaise(@Payload TeamRaiseRequest request) {
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + request.getRoomId() + "/raise",
                request.getUserId()
        );
    }

    /** ✅ 발표 시작 */
    @MessageMapping("/study/team/present/start")
    public void handleStartPresentation(@Payload PresentationStartRequest request) {
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + request.getRoomId() + "/presentation/start",
                request
        );
    }

    /** ✅ 발표 종료 → 투표 시작 */
    @MessageMapping("/study/team/present/end")
    public void handleEndPresentation(@Payload PresentationEndRequest request) {
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + request.getRoomId() + "/vote",
                request
        );
    }

    /** ✅ 투표 수신 */
    @MessageMapping("/study/team/vote")
    public void handleVote(@Payload VoteMessage vote) {
        boolean isSuccess = socketService.collectVote(vote); // 과반 체크 포함
        if (isSuccess) {
            messagingTemplate.convertAndSend(
                    "/sub/study/team/" + vote.getRoomId() + "/vote/result",
                    "SUCCESS"
            );
        } else {
            messagingTemplate.convertAndSend(
                    "/sub/study/team/" + vote.getRoomId() + "/vote/result",
                    "FAIL"
            );
        }
    }

    /** ✅ Focus 모드: 집중 시간 갱신 */
    @MessageMapping("/study/team/focus/update")
    public void updateFocusTime(@Payload FocusTimeUpdate update) {
        socketService.updateFocusTime(update);
        messagingTemplate.convertAndSend(
                "/sub/study/team/" + update.getRoomId() + "/rank",
                socketService.getCurrentRanking(update.getRoomId())
        );
    }
}
