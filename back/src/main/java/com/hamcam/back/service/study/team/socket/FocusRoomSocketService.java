package com.hamcam.back.service.study.team.socket;

import com.hamcam.back.dto.study.team.socket.response.FocusRankingResponse;
import com.hamcam.back.dto.study.team.socket.response.FocusRankingResponse.FocusRankingEntry;
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

    // ✅ 각 방별 집중 시간 저장: roomId → userId → 누적시간
    private final Map<Long, Map<Long, Integer>> focusTimeMap = new ConcurrentHashMap<>();

    // ✅ 각 방별 목표 달성 여부: roomId → userId Set
    private final Map<Long, Set<Long>> goalAchievedMap = new ConcurrentHashMap<>();

    // ✅ 각 방별 종료 확인 유저: roomId → userId Set
    private final Map<Long, Set<Long>> confirmExitMap = new ConcurrentHashMap<>();

    /**
     * ✅ 방 입장 처리 (유저 초기화)
     */
    public void enterRoom(Long roomId, Long userId) {
        focusTimeMap.putIfAbsent(roomId, new ConcurrentHashMap<>());
        focusTimeMap.get(roomId).putIfAbsent(userId, 0);
    }

    /**
     * ✅ 집중 시간 업데이트 (1분마다 호출됨)
     */
    public void updateFocusTime(Long roomId, Long userId, int deltaSeconds) {
        Map<Long, Integer> userTimes = focusTimeMap.get(roomId);
        if (userTimes != null) {
            userTimes.put(userId, userTimes.getOrDefault(userId, 0) + deltaSeconds);
        }
    }

    /**
     * ✅ 현재 랭킹 응답 반환
     */
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
     * ✅ 목표 시간 도달 시 호출됨
     */
    public void markGoalAchieved(Long roomId, Long userId) {
        goalAchievedMap.putIfAbsent(roomId, new HashSet<>());
        goalAchievedMap.get(roomId).add(userId);
    }

    /**
     * ✅ 유저가 결과 화면에서 “확인” 클릭 → 모든 유저 확인 시 방 삭제
     */
    public void confirmExit(Long roomId, Long userId) {
        confirmExitMap.putIfAbsent(roomId, new HashSet<>());
        confirmExitMap.get(roomId).add(userId);

        int participantCount = focusTimeMap.getOrDefault(roomId, Collections.emptyMap()).size();
        int confirmed = confirmExitMap.get(roomId).size();

        if (confirmed >= participantCount) {
            // ✅ 모든 참가자가 결과 확인 → 방 종료 처리
            focusTimeMap.remove(roomId);
            goalAchievedMap.remove(roomId);
            confirmExitMap.remove(roomId);
            focusRoomRepository.deleteById(roomId);
        }
    }
}
