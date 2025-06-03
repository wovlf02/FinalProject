package com.hamcam.back.service.study.team.socket;

import com.hamcam.back.dto.study.team.socket.response.FocusRankingResponse;
import com.hamcam.back.dto.study.team.socket.response.FocusRankingResponse.FocusRankingEntry;
import com.hamcam.back.dto.study.team.socket.response.ParticipantInfo;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.FocusRoom;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.FocusRoomRepository;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FocusRoomSocketService {

    private final FocusRoomRepository focusRoomRepository;
    private final UserRepository userRepository;

    private final Map<Long, Map<Long, Integer>> focusTimeMap = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> goalAchievedMap = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> confirmExitMap = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> warningsMap = new ConcurrentHashMap<>(); // 경고 기록
    private final Map<Long, Long> winnerMap = new ConcurrentHashMap<>(); // roomId → 승리자 userId

    public void enterRoom(Long roomId, Long userId) {
        focusTimeMap.putIfAbsent(roomId, new ConcurrentHashMap<>());
        focusTimeMap.get(roomId).putIfAbsent(userId, 0);
    }

    public void updateFocusTime(Long roomId, Long userId, int deltaSeconds) {
        Map<Long, Integer> userTimes = focusTimeMap.get(roomId);
        if (userTimes != null) {
            userTimes.put(userId, userTimes.getOrDefault(userId, 0) + deltaSeconds);
        }
    }

    public FocusRankingResponse getCurrentRanking(Long roomId) {
        Map<Long, Integer> userTimes = focusTimeMap.getOrDefault(roomId, Collections.emptyMap());
        Set<Long> goalSet = goalAchievedMap.getOrDefault(roomId, Collections.emptySet());

        List<FocusRankingEntry> rankings = userTimes.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    int totalSeconds = entry.getValue();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                    return FocusRankingEntry.builder()
                            .nickname(user.getNickname())
                            .totalSeconds(totalSeconds)
                            .goalAchieved(goalSet.contains(userId))
                            .build();
                })
                .sorted((a, b) -> Integer.compare(b.getTotalSeconds(), a.getTotalSeconds()))
                .collect(Collectors.toList());

        return FocusRankingResponse.builder()
                .roomId(roomId)
                .rankings(rankings)
                .build();
    }

    /**
     * ✅ 최초 도달자 여부를 반환 (true면 브로드캐스트 대상)
     */
    public boolean markGoalAchieved(Long roomId, Long userId) {
        goalAchievedMap.putIfAbsent(roomId, new HashSet<>());
        Set<Long> achievers = goalAchievedMap.get(roomId);
        boolean isFirst = achievers.isEmpty();

        achievers.add(userId);

        if (isFirst) {
            winnerMap.put(roomId, userId);
        }

        return isFirst;
    }

    /**
     * ✅ 결과 확인 → 저장만 (컨트롤러에서 확인자 수 비교)
     */
    public void confirmExit(Long roomId, Long userId) {
        confirmExitMap.putIfAbsent(roomId, new HashSet<>());
        confirmExitMap.get(roomId).add(userId);
    }

    /**
     * ✅ 전체 확인 여부 판별
     */
    public boolean isAllConfirmed(Long roomId) {
        int total = focusTimeMap.getOrDefault(roomId, Collections.emptyMap()).size();
        int confirmed = confirmExitMap.getOrDefault(roomId, Collections.emptySet()).size();
        return confirmed >= total;
    }

    /**
     * ✅ 모든 데이터 삭제 (방 종료)
     */
    public void deleteRoomData(Long roomId) {
        focusTimeMap.remove(roomId);
        goalAchievedMap.remove(roomId);
        confirmExitMap.remove(roomId);
        warningsMap.remove(roomId);
        winnerMap.remove(roomId);
        focusRoomRepository.deleteById(roomId);
    }

    /**
     * ✅ 방장이 맞는지 확인
     */
    public boolean isHost(Long roomId, Long userId) {
        FocusRoom room = focusRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_ROOM_NOT_FOUND));
        return Objects.equals(room.getHostId(), userId);
    }

    /**
     * ✅ 강제 종료 (방장에 의해)
     */
    public void terminateRoom(Long roomId) {
        deleteRoomData(roomId);
    }

    /**
     * ✅ 자리비움/졸음 감지 경고 누적
     */
    public void accumulateWarning(Long roomId, Long userId, String reason) {
        warningsMap.putIfAbsent(roomId, new HashSet<>());
        warningsMap.get(roomId).add(userId);
    }

    /**
     * ✅ 실시간 참가자 목록 생성
     */
    public List<ParticipantInfo> getCurrentParticipants(Long roomId) {
        Map<Long, Integer> userTimes = focusTimeMap.getOrDefault(roomId, Collections.emptyMap());
        Set<Long> confirmedSet = confirmExitMap.getOrDefault(roomId, Collections.emptySet());

        return userTimes.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    int seconds = entry.getValue();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    return ParticipantInfo.builder()
                            .userId(userId)
                            .nickname(user.getNickname())
                            .focusedSeconds(seconds)
                            .confirmed(confirmedSet.contains(userId))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
