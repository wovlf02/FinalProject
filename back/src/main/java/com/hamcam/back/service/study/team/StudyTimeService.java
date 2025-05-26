package com.hamcam.back.service.study.team;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StudyTimeService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX_TOTAL = "study:total"; // Hash: roomId_userId -> minutes
    private static final String PREFIX_START = "study:start"; // Key: roomId_userId -> timestamp

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 생성자에서 @Qualifier 적용!
    public StudyTimeService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 공부 시작 시간 기록
     */
    public void recordStartTime(Long roomId, Long userId, LocalDateTime now) {
        String key = roomId + "_" + userId;
        redisTemplate.opsForValue().set(PREFIX_START + ":" + key, now.format(formatter));
    }

    /**
     * 누적 공부 시간 추가
     */
    public void addStudyTime(Long roomId, Long userId, int minutes) {
        String key = roomId + "_" + userId;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String totalStr = hashOps.get(PREFIX_TOTAL, key);
        int prev = (totalStr != null) ? Integer.parseInt(totalStr) : 0;
        hashOps.put(PREFIX_TOTAL, key, String.valueOf(prev + minutes));
    }

    /**
     * 총 공부 시간 조회
     */
    public int getTotalMinutes(Long roomId, Long userId) {
        String key = roomId + "_" + userId;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String val = hashOps.get(PREFIX_TOTAL, key);
        return (val != null) ? Integer.parseInt(val) : 0;
    }

    /**
     * 모든 유저 공부 시간 조회 (랭킹용)
     */
    public Map<Long, Integer> getAllUserStudyTimes(Long roomId) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Map<String, String> all = hashOps.entries(PREFIX_TOTAL);
        Map<Long, Integer> result = new HashMap<>();

        for (Map.Entry<String, String> entry : all.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("_");
            if (parts.length != 2) continue;

            Long rId = Long.parseLong(parts[0]);
            Long uId = Long.parseLong(parts[1]);
            if (rId.equals(roomId)) {
                result.put(uId, Integer.parseInt(entry.getValue()));
            }
        }
        return result;
    }
}
