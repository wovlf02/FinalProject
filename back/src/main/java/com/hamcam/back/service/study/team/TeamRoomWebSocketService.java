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

    /**
     * 문제풀이 시작 알림 처리
     */
    public void startQuiz(TeamRoomStompMessage message) {
        log.info("🚀 문제풀이 시작 알림 - roomId: {}", message.getRoomId());
        // 필요한 경우 상태를 캐싱하거나 로깅 또는 외부 연동 수행 가능
    }

    /**
     * 문제풀이 종료 알림 처리
     */
    public void terminateQuiz(TeamRoomStompMessage message) {
        log.info("🛑 문제풀이 종료 알림 - roomId: {}", message.getRoomId());
        // 종료 시 클라이언트에 종료 알림용 데이터 캐싱 또는 처리 가능
    }

    /**
     * 실패한 문제 커뮤니티 업로드 알림 처리
     */
    public void uploadUnsolvedQuestion(TeamRoomStompMessage message) {
        log.info("📮 실패한 문제 업로드 알림 - roomId: {}, userId: {}", message.getRoomId(), message.getUserId());
        // 포인트 차감 여부나 커뮤니티 연동 결과 등을 추적하려면 이곳에서 처리
    }

    /**
     * 공부시간 경쟁 종료 알림 처리
     */
    public void completeFocus(TeamRoomStompMessage message) {
        log.info("🏁 공부시간 경쟁방 종료 알림 - roomId: {}", message.getRoomId());
        // 최종 랭킹 계산된 결과를 여기서 받아 클라이언트 전송 처리 가능
    }

}
