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

    /** ✅ 채팅 메시지 저장 */
    public void saveChatMessage(TeamChatMessage message) {
        String key = "study_chat:" + message.getRoomId();
        redisService.pushList(key, message);
    }

    /** ✅ 투표 결과 수집 + 과반수 판단 */
    public boolean collectVote(VoteMessage vote) {
        String key = "quiz_vote:" + vote.getRoomId();
        redisService.incrementHash(key, vote.getUserId().toString(), vote.isSuccess() ? 1 : 0);

        Map<String, Integer> votes = redisService.getHashAsIntMap(key);
        int total = votes.size();
        long successCount = votes.values().stream().filter(v -> v == 1).count();

        return successCount > total / 2;
    }

    /** ✅ Focus 모드 시간 갱신 */
    public void updateFocusTime(FocusTimeUpdate update) {
        String key = "focus_timer:" + update.getRoomId() + ":" + update.getUserId();
        redisService.incrementValue(key, update.getDeltaMinutes());
    }

    /** ✅ Focus 모드 랭킹 조회 */
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
