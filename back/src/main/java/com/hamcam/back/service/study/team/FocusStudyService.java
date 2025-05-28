package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.rest.response.inner.RankingInfo;
import com.hamcam.back.entity.study.team.FocusRoom;
import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.FocusRoomRepository;
import com.hamcam.back.repository.study.StudyRoomParticipantRepository;
import com.hamcam.back.util.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FocusStudyService {

    private final FocusRoomRepository focusRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    /** ✅ 유저의 집중 시간 업데이트 (Redis: room:{roomId}:focus:{userId} = 누적 시간) */
    public void updateFocusTime(Long roomId, Long userId, int focusTime) {
        String key = getFocusKey(roomId, userId);
        redisService.increaseValue(key, focusTime);
    }

    /** ✅ 실시간 랭킹 반환 (Redis 기준) */
    public List<RankingInfo> generateRanking(Long roomId) {
        List<StudyRoomParticipant> participants = getParticipants(roomId);

        List<RankingInfo> ranking = participants.stream()
                .map(p -> {
                    Long userId = p.getUser().getId();
                    String nickname = p.getUser().getNickname();
                    int time = redisService.getIntValue(getFocusKey(roomId, userId));
                    return RankingInfo.of(userId, nickname, time);
                })
                .sorted((a, b) -> Integer.compare(b.getFocusTime(), a.getFocusTime()))
                .collect(Collectors.toList());

        return ranking;
    }

    /** ✅ 목표 시간 도달자 판별 */
    public Optional<RankingInfo> checkWinner(Long roomId) {
        FocusRoom room = focusRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        int targetTime = room.getTargetTime();
        List<StudyRoomParticipant> participants = getParticipants(roomId);

        for (StudyRoomParticipant p : participants) {
            Long userId = p.getUser().getId();
            int time = redisService.getIntValue(getFocusKey(roomId, userId));
            if (time >= targetTime) {
                return Optional.of(RankingInfo.of(userId, p.getUser().getNickname(), time));
            }
        }
        return Optional.empty();
    }

    /** ✅ 방 종료 처리 (방 비활성화 + Redis 삭제) */
    public void closeRoom(Long roomId) {
        FocusRoom room = focusRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        room.deactivate();
        redisService.deleteChatLog(roomId); // 채팅 로그
        redisService.deleteFocusRoomData(roomId); // 집중 시간 기록 삭제
    }

    /** ✅ 공통 유틸 */
    private List<StudyRoomParticipant> getParticipants(Long roomId) {
        FocusRoom room = focusRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        return participantRepository.findByRoom(room);
    }

    private String getFocusKey(Long roomId, Long userId) {
        return "focus:" + roomId + ":" + userId;
    }
}
