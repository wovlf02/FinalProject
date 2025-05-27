package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.response.RankingResponse;
import com.hamcam.back.entity.video.Participant;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.video.ParticipantRepository;
import com.hamcam.back.repository.video.VideoRoomRepository;
import com.hamcam.back.util.RedisKeyUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ✅ 집중 시간 경쟁방 실시간 랭킹 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FocusRankingService {

    private final ParticipantRepository participantRepository;
    private final VideoRoomRepository videoRoomRepository;
    private final RedisTemplate<String, Integer> redisTemplate;

    /**
     * ✅ 집중 시간 추가
     * - 캠 동작 인식 → 일정 주기마다 초 단위로 누적
     */
    public void updateFocusTime(Long userId, Long roomId, int seconds) {
        String key = RedisKeyUtil.focusKey(roomId, userId);
        redisTemplate.opsForValue().increment(key, seconds);
        redisTemplate.expire(key, 6, TimeUnit.HOURS); // TTL 설정
    }

    /**
     * ✅ 실시간 랭킹 조회 (목표 시간 기준 도달 여부 포함)
     */
    public RankingResponse getRanking(Long roomId, int targetSeconds) {
        List<Participant> participants = participantRepository.findAllByRoomId(roomId);

        List<RankingResponse.RankingEntry> ranking = participants.stream()
                .map(p -> {
                    String key = RedisKeyUtil.focusKey(roomId, p.getUser().getId());
                    Integer focusTime = redisTemplate.opsForValue().get(key);
                    int totalSeconds = (focusTime != null) ? focusTime : 0;

                    return RankingResponse.RankingEntry.builder()
                            .userId(p.getUser().getId())
                            .nickname(p.getUser().getNickname())
                            .focusTime(totalSeconds)
                            .finished(totalSeconds >= targetSeconds)
                            .build();
                })
                .sorted(Comparator.comparingInt(RankingResponse.RankingEntry::getFocusTime).reversed())
                .collect(Collectors.toList());

        return RankingResponse.builder()
                .roomId(roomId)
                .ranking(ranking)
                .build();
    }

    /**
     * ✅ 목표 시간 도달 유저가 있는지 확인 후 방 종료 및 Redis 정리
     * @return 가장 먼저 도달한 유저의 userId 또는 null
     */
    public Long checkForWinnerAndEndRoom(Long roomId, int targetSeconds) {
        VideoRoom room = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.isActive()) return null;

        List<Participant> participants = participantRepository.findAllByRoomId(roomId);

        Participant winner = participants.stream()
                .map(p -> {
                    String key = RedisKeyUtil.focusKey(roomId, p.getUser().getId());
                    Integer time = redisTemplate.opsForValue().get(key);
                    return new Object[] { p, (time != null ? time : 0) };
                })
                .filter(arr -> (Integer) arr[1] >= targetSeconds)
                .sorted((a, b) -> Integer.compare((Integer) b[1], (Integer) a[1])) // 도달 시간 순
                .map(arr -> (Participant) arr[0])
                .findFirst()
                .orElse(null);

        if (winner != null) {
            room.setActive(false);
            videoRoomRepository.save(room);

            // ✅ 포인트 지급 로직 (포인트 서비스 연동 시 확장)
            // pointService.givePoint(winner.getUser().getId(), WINNER_POINT);

            // ✅ Redis 집중 시간 기록 삭제
            for (Participant p : participants) {
                redisTemplate.delete(RedisKeyUtil.focusKey(roomId, p.getUser().getId()));
            }

            return winner.getUser().getId();
        }

        return null;
    }
}
