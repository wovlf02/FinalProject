package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.request.SignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * ✅ SFU signaling 메시지 중계 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SfuSignalService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ✅ signaling 메시지를 /sub/signal/user/{to} 경로로 전송
     */
    public void relaySignal(SignalMessage message) {
        if (message == null || message.getTo() == null || message.getTo().isBlank()) {
            log.warn("❌ [Signal] 메시지 무효: to=null 또는 형식 오류");
            return;
        }

        String destination = "/sub/signal/user/" + message.getTo();

        try {
            log.debug("📨 [Signal] {} → {} 전송 (type={})", message.getFrom(), message.getTo(), message.getType());
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("❌ [Signal] 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }
}
