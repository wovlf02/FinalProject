package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.socket.*;
import com.hamcam.back.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamStudySocketService {

    private final RedisService redisService;

    /** ✅ 채팅 메시지 Redis 저장 */
    public void saveChatMessage(TeamChatMessage message) {
        String key = "study_chat:" + message.getRoomId();
        redisService.pushList(key, message); // List 구조 저장
    }

    /** ✅ 발표 투표 수집 + 과반 판단 */
    public boolean collectVote(VoteMessage vote) {
        String key = "quiz_vote:" + vote.getRoomId();
        redisService.incrementHash(key, vote.getUserId().toString(), vote.isSuccess() ? 1 : 0);

        Map<String, Integer> votes = redisService.getHashAsIntMap(key);
        int total = votes.size();
        long successCount = votes.values().stream().filter(v -> v == 1).count();

        // ✅ 과반수 이상 성공 판정
        return successCount > total / 2;
    }

    /** ✅ Focus 모드 집중 시간 업데이트 */
    public void updateFocusTime(FocusTimeUpdate update) {
        String key = "focus_timer:" + update.getRoomId() + ":" + update.getUserId();
        redisService.incrementValue(key, update.getDeltaMinutes());
    }

    /** ✅ Focus 모드 현재 랭킹 조회 */
    public List<FocusRankResponse> getCurrentRanking(Long roomId) {
        String prefix = "focus_timer:" + roomId + ":";
        Map<String, Integer> userTimeMap = redisService.scanByPrefix(prefix); // key → time

        List<FocusRankResponse> ranks = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : userTimeMap.entrySet()) {
            Long userId = Long.parseLong(entry.getKey().replace(prefix, ""));
            ranks.add(new FocusRankResponse(userId, entry.getValue()));
        }

        ranks.sort((a, b) -> b.getMinutes() - a.getMinutes());
        return ranks;
    }
}
