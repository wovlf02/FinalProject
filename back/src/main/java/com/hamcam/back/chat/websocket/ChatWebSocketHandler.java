package com.hamcam.back.chat.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [ChatWebSocketHandler]
 *
 * Spring WebSocket 연결/해제 이벤트를 처리하는 핵심 핸들러 클래스
 * 사용자가 채팅방에 연결되거나 나갈 때 WebSocket 세션을 추적하며,
 * 연결된 사용자에 대한 세션 정보와 사용자 ID, 채팅방 ID를 기반으로
 * 참여자 관리(ChatRoomManager 연동)를 수행함
 *
 * 주요 기능:
 * -> WebSocket 연결 시 사용자 참여 등록
 * -> 연결 해제 시 사용자 퇴장 처리
 * -> 실시간 참여자 수 추적
 *
 * 메시지 송수신은 STOMP 기반으로 다른 구성에서 처리되며,
 * 이 핸들러는 WebSocket 연결 수명 주기를 관리하는 데 중점을 둠
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatRoomManager chatRoomManager;

    // 세션 ID -> 사용자 ID 매핑
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    // 세션 ID -> 채팅방 ID 매핑
    private final Map<String, Long> sessionRoomMap = new ConcurrentHashMap<>();

    /**
     * [WebSocket 연결 시 호출]
     *
     * 클라이언트가 WebSocket에 처음 연결하면 호출되며,
     * QueryString 또는 Header에서 전달된 userId, roomId를 추출하여 참여자 목록에 등록
     *
     * @param session WebSocketSession 연결 객체
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Map<String, String> params = parseQueryString(session.getUri().getQuery());

            Long userId = Long.parseLong(params.get("userId"));
            Long roomId = Long.parseLong(params.get("roomId"));

            sessionUserMap.put(session.getId(), userId);
            sessionRoomMap.put(session.getId(), roomId);

            chatRoomManager.joinRoom(roomId, userId);

            log.info("🔌 WebSocket 연결: userId={}, roomId={}, sessionId={}",
                    userId, roomId, session.getId());

        } catch (Exception e) {
            log.error("❌ WebSocket 연결 실패: {}", e.getMessage());
        }
    }

    /**
     * [WebSocket 연결 종료 시 호출]
     *
     * 클라이언트가 WebSocket 연결을 종료하면 호출되며,
     * session ID를 기준으로 사용자 및 채팅방 정보를 제거하고,
     * 참여자 관리 목록에서 사용자 제거
     *
     * @param session WebsocketSession 연결 객체
     * @param status CloseStatus 종료 상태
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();

        Long userId = sessionUserMap.remove(sessionId);
        Long roomId = sessionRoomMap.remove(sessionId);

        if(roomId != null && userId != null) {
            chatRoomManager.leaveRoom(roomId, userId);
            log.info("❎ WebSocket 종료: userId={}, roomId={}, sessionId={}", userId, roomId, sessionId);
        }
    }

    /**
     * [WebSocket 오류 발생 시]
     *
     * @param session 연결 세션
     * @param exception 예외 정보
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("⚠️ WebSocket 에러: sessionId={}, error={}", session.getId(), exception.getMessage());
    }

    /**
     * [QueryString 파서]
     *
     * userId, roomId를 URI 쿼리 문자열에서 추출하는 헬퍼 함수
     *
     * @param queryString ex) "userId-3&roomId=5"
     * @return Map<String, String> key-value 파싱 결과
     */
    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> result = new ConcurrentHashMap<>();
        if(queryString == null) return result;

        String[] pairs = queryString.split("&");
        for(String pair : pairs) {
            String[] parts = pair.split("=");
            if(parts.length == 2) {
                result.put(parts[0], parts[1]);
            }
        }
        return result;
    }
}
