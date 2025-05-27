package com.hamcam.back.controller.video;

import com.hamcam.back.dto.video.request.SignalMessage;
import com.hamcam.back.service.video.SfuSignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * ✅ SFU signaling 메시지 중계 WebSocket Controller
 * - 클라이언트의 SDP / ICE signaling 메시지를 중계함
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class SfuSignalController {

    private final SfuSignalService sfuSignalService;

    /**
     * ✅ 클라이언트 → 서버로 signaling 메시지 전송
     * - 목적지: /pub/signal/send
     */
    @MessageMapping("/signal/send")
    public void handleSignal(
            @Payload SignalMessage message,
            SimpMessageHeaderAccessor headers
    ) {
        log.debug("📡 Signal received | type={}, from={}, to={}, room={}",
                message.getType(), message.getFrom(), message.getTo(), message.getRoomId());

        sfuSignalService.relaySignal(message);
    }
}
