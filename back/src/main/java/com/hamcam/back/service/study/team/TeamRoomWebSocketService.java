package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.websocket.TeamRoomStompMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [TeamRoomWebSocketService]
 * 팀 학습방(WebSocket 기반) 실시간 기능 처리 서비스
 * - 손들기, 발표자 지정, 투표, 랭킹
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamRoomWebSocketService {

    /**
     * 각 방 별 손든 사용자 목록 (메모리 기반)
     */
    private final Map<Long, Set<Long>> raisedHandsMap = new ConcurrentHashMap<>();

    /**
     * 각 방 별 현재 발표자
     */
    private final Map<Long, Long> presenterMap = new ConcurrentHashMap<>();

    /**
     * 각 방 별 투표 결과 저장
     * Key: roomId, Value: Map<userId, voteResult>
     */
    private final Map<Long, Map<Long, Boolean>> voteResultsMap = new ConcurrentHashMap<>();

    /**
     * 손들기 요청 처리
     */
    public void handleRaiseHand(TeamRoomStompMessage message) {
        raisedHandsMap
                .computeIfAbsent(message.getRoomId(), k -> ConcurrentHashMap.newKeySet())
                .add(message.getUserId());
        log.info("🙋 손든 사용자 리스트: {}", raisedHandsMap.get(message.getRoomId()));
    }

    /**
     * 발표자 지정 처리
     */
    public void setPresenter(TeamRoomStompMessage message) {
        presenterMap.put(message.getRoomId(), message.getUserId());
        log.info("🗣 발표자 설정 - roomId: {}, presenterId: {}", message.getRoomId(), message.getUserId());
    }

    /**
     * 투표 시작 시 기존 기록 초기화
     */
    public void startVoting(TeamRoomStompMessage message) {
        voteResultsMap.put(message.getRoomId(), new ConcurrentHashMap<>());
        log.info("🗳 투표 초기화 완료 - roomId: {}", message.getRoomId());
    }

    /**
     * 투표 응답 수집 및 처리
     */
    public void processVoteResponse(TeamRoomStompMessage message) {
        voteResultsMap
                .computeIfAbsent(message.getRoomId(), k -> new ConcurrentHashMap<>())
                .put(message.getUserId(), message.getVoteResult());

        int totalVotes = voteResultsMap.get(message.getRoomId()).size();
        long successCount = voteResultsMap.get(message.getRoomId()).values().stream().filter(Boolean::booleanValue).count();

        log.info("✅ 현재 투표 수: {}, 성공 투표 수: {}", totalVotes, successCount);

        // 여기서 과반수 여부 판단 및 발표자에게 포인트 지급 로직 추가 가능
    }

    /**
     * 실시간 랭킹 전송용 데이터 가공 (샘플)
     */
    public void broadcastRanking(TeamRoomStompMessage message) {
        log.info("📊 랭킹 업데이트 요청 수신 - roomId: {}", message.getRoomId());
        // 실제 랭킹 데이터는 다른 서비스에서 계산해 전달하는 방식 권장
    }
}
