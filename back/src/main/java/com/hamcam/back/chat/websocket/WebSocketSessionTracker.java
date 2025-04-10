package com.hamcam.back.chat.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [WebSocketSessionTracker]
 *
 * WebSocket 연결 세션을 메모리 상에서 추적 및 관리하는 유틸리티 클래스
 * 각 사용자 또는 채팅방 기준으로 현재 연결된 세션 목록을 확인하거나,
 * 특정 대상에게 메시지를 전송하기 위한 세션을 탐색할 수 있도록 도움
 *
 * 주요 기능:
 * -> 사용자 ID 기반 세션 등록 및 제거
 * -> 채팅방 ID 기반 세션 추적
 * -> 세션 ID -> 사용자 및 채팅방 매핑
 * -> 실시간 참여자 수 확인 및 디버깅용 조회
 *
 * 사용 예:
 * -> 메시지 브로드캐스트 시 대상 사용자에게 직접 전달
 * -> 사용자 접속 여부 판단
 */
@Slf4j
@Component
public class WebSocketSessionTracker {

    // 사용자 ID -> WebSocket 세션 목록
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    // 채팅방 ID -> WebSocket 세션 목록
    private final Map<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 세션 ID -> 사용자 ID
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    // 세션 ID -> 채팅방 ID
    private final Map<String, Long> sessionRoomMap = new ConcurrentHashMap<>();

    /**
     * [세션 등록]
     * 새로운 WebSocket 세션이 생성되었을 때 사용자 및 채팅방 기준으로 등록
     *
     * @param session WebSocket 세션
     * @param userId 사용자 ID
     * @param roomId 채팅방 ID
     */
    public void registerSession(WebSocketSession session, Long userId, Long roomId) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        sessionUserMap.put(session.getId(), userId);
        sessionRoomMap.put(session.getId(), roomId);

        log.info("✅ 세션 등록: sessionId={}, userId={}, roomId={}", session.getId(), userId, roomId);
    }

    /**
     * [세션 제거]
     * WebSocket 연결이 종료되었을 때 내부 세션 목록에서 제거
     *
     * @param session 종료된 WebSocket 세션
     */
    public void unregisterSession(WebSocketSession session) {
        String sessionId = session.getId();

        Long userId = sessionUserMap.remove(sessionId);
        Long roomId = sessionRoomMap.remove(sessionId);

        if (userId != null) {
            Set<WebSocketSession> userSet = userSessions.get(userId);
            if(userSet != null) {
                userSet.remove(session);
                if(userSet.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }

        if(roomId != null) {
            Set<WebSocketSession> roomSet = roomSessions.get(roomId);
            if(roomSet != null) {
                roomSet.remove(session);
                if(roomSet.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
        }

        log.info("❎ 세션 해제: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId);
    }

    /**
     * [사용자 ID 기준 세션 목록 조회]
     *
     * @param userId 사용자 ID
     * @return 연결된 세션 목록 (없을 경우 빈 Set)
     */
    public Set<WebSocketSession> getSessionByUser(Long userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    /**
     * [채팅방 ID 기준 세션 목록 조회]
     *
     * @param roomId 채팅방 ID
     * @return 연결된 세션 목록 (없을 경우 빈 Set)
     */
    public Set<WebSocketSession> getSessionsByRoom(Long roomId) {
        return roomSessions.getOrDefault(roomId, Collections.emptySet());
    }

    /**
     * [세션 ID -> 사용자 ID 조회]
     *
     * @param sessionId 세션 ID
     * @return 사용자 ID 또는 null
     */
    public Long getUserIdBySession(String sessionId) {
        return sessionUserMap.get(sessionId);
    }

    /**
     * [세션 ID -> 채팅방 ID 조회]
     *
     * @param sessionId 세션 ID
     * @return 채팅방 ID 또는 null
     */
    public Long getRoomIdBySession(String sessionId) {
        return sessionRoomMap.get(sessionId);
    }
}
