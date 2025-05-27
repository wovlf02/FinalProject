package com.hamcam.back.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ✅ SFU WebRTC 세션 매핑 관리자
 * - socketId ↔ userId ↔ roomId 연결 관리
 */
@Component
public class SfuSessionManager {

    /** socketId -> userId */
    private final Map<String, Long> socketToUser = new ConcurrentHashMap<>();

    /** socketId -> roomId */
    private final Map<String, Long> socketToRoom = new ConcurrentHashMap<>();

    /** userId -> socketId */
    private final Map<Long, String> userToSocket = new ConcurrentHashMap<>();

    /** roomId -> Map<userId, socketId> */
    private final Map<Long, Map<Long, String>> roomParticipants = new ConcurrentHashMap<>();

    /**
     * ✅ 유저 입장 시 등록
     */
    public void registerSession(String socketId, Long userId, Long roomId) {
        socketToUser.put(socketId, userId);
        socketToRoom.put(socketId, roomId);
        userToSocket.put(userId, socketId);

        roomParticipants.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(userId, socketId);
    }

    /**
     * ✅ socketId로 userId 조회
     */
    public Long getUserId(String socketId) {
        return socketToUser.get(socketId);
    }

    /**
     * ✅ socketId로 roomId 조회
     */
    public Long getRoomId(String socketId) {
        return socketToRoom.get(socketId);
    }

    /**
     * ✅ userId로 socketId 조회
     */
    public String getSocketId(Long userId) {
        return userToSocket.get(userId);
    }

    /**
     * ✅ 특정 방 참가자 목록 반환 (userId -> socketId)
     */
    public Map<Long, String> getParticipants(Long roomId) {
        return roomParticipants.getOrDefault(roomId, Map.of());
    }

    /**
     * ✅ 연결 종료 시 정리
     */
    public void removeSession(String socketId) {
        Long userId = socketToUser.remove(socketId);
        Long roomId = socketToRoom.remove(socketId);

        if (userId != null) {
            userToSocket.remove(userId);
        }

        if (roomId != null && userId != null) {
            Map<Long, String> participants = roomParticipants.get(roomId);
            if (participants != null) {
                participants.remove(userId);
                if (participants.isEmpty()) {
                    roomParticipants.remove(roomId);
                }
            }
        }
    }

    /**
     * ✅ 방 완전 종료 시 모든 세션 제거
     */
    public void removeRoom(Long roomId) {
        Map<Long, String> participants = roomParticipants.remove(roomId);
        if (participants != null) {
            for (String socketId : participants.values()) {
                socketToUser.remove(socketId);
                socketToRoom.remove(socketId);
            }
            for (Long userId : participants.keySet()) {
                userToSocket.remove(userId);
            }
        }
    }
}
