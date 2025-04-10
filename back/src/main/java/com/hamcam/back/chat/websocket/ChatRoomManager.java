package com.hamcam.back.chat.websocket;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [ChatRoomManager]
 *
 * 실시간 채팅방에 참여 중인 사용자 정보를 메모리 상에서 관리하는 유틸리티 클래스
 * WebSocket 기반 채팅에서 DB 대신 빠르게 접근할 수 있는 구조를 제공하며,
 * 채팅방을 입장/퇴장 인원 추적, 실시간 참여자 수 조회 등에 활용됨
 *
 * 이 클래스는 WebSocket 연결 시점에 호출되어 사용자의 입장을 기록하고,
 * 연결 종료나 퇴장 시점에 사용자를 제거하여 참여 상태를 반영함
 *
 * [주요 기능]
 * -> 채팅방별 참여자 목록 저장 (roomId -> Set<userId>)
 * -> 채팅방 입장 / 퇴장 처리
 * -> 채팅방 내 전체 참여자 목록, 인원 수 조회
 *
 * 서버 재시작 시 정보는 초기화되는 캐시 성격으로 사용됨
 */
@Component
public class ChatRoomManager {

    // 채팅방 ID -> 사용자 ID Set 매핑 구조 (스레드 안전성 보장)
    private final Map<Long, Set<Long>> roomUserMap = new ConcurrentHashMap<>();

    /**
     * [채팅방 입장]
     * 
     * 사용자가 특정 채팅방에 입장할 때 호출
     * 해당 채팅방에 사용자 ID를 등록
     * 
     * @param roomId 입장할 채팅방 ID
     * @param userId 입장하는 사용자 ID
     */
    public void joinRoom(Long roomId, Long userId) {
        roomUserMap.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    /**
     * [채팅방 퇴장]
     * 
     * 사용자가 채팅방에서 퇴장할 때 호출
     * 해당 채팅방의 참여자 목록에서 사용자 ID를 제거함
     * 만약 퇴장 후 참여자가 없다면 roomId 자체를 삭제함
     * 
     * @param roomId 퇴장할 채팅방 ID
     * @param userId 퇴장하는 사용자 ID
     */
    public void leaveRoom(Long roomId, Long userId) {
        Set<Long> users = roomUserMap.get(roomId);
        if(users != null) {
            users.remove(userId);
            if(users.isEmpty()) {
                roomUserMap.remove(roomId); // 참여자가 없는 방 제거
            }
        }
    }

    /**
     * [채팅방 참여자 수 조회]
     * 
     * 현재 채팅방에 참여 중인 사용자 수를 반환
     * 
     * @param roomId 채팅방 ID
     * @return 참여 중인 사용자 수 (int)
     */
    public int getParticipantCount(Long roomId) {
        return roomUserMap.getOrDefault(roomId, Collections.emptySet()).size();
    }

    /**
     * [채팅방 참여자 목록 조회]
     * 
     * 채팅방에 현재 참여 중인 모든 사용자 ID를 반환
     * 
     * @param roomId 채팅방 ID
     * @return Set<Long> 사용자 ID 목록 (빈 Set일 수 있음)
     */
    public Set<Long> getUsersInRoom(Long roomId) {
        return roomUserMap.getOrDefault(roomId, Collections.emptySet());
    }

    /**
     * [전체 채팅방 현황 확인 (디버그용)]
     * 
     * 현재 메모리에 등록된 모든 채팅방 ID와 참여자 목록을 조회
     * 콘솔 디버깅 및 모니터링 용도
     */
    public void printAllRoomsStatus() {
        roomUserMap.forEach((roomId, users) -> {
            System.out.println("Room " + roomId + ": " + users.size() + " usrs -> " + users);
        });
    }
}
