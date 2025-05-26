package com.hamcam.back.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private ListOperations<String, String> listOps;
    private HashOperations<String, String, String> hashOps;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    public void init() {
        listOps = redisTemplate.opsForList();
        hashOps = redisTemplate.opsForHash();
        valueOps = redisTemplate.opsForValue();
    }

    /** ✅ 리스트에 데이터 추가 (채팅 등) */
    public <T> void pushList(String key, T data) {
        try {
            String value = objectMapper.writeValueAsString(data);
            listOps.rightPush(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Redis 리스트 저장 실패", e);
        }
    }

    /** ✅ 해시맵 값 증가 (투표 등) */
    public void incrementHash(String key, String field, int increment) {
        hashOps.increment(key, field, increment);
    }

    /** ✅ 해시맵 → Map<String, Integer> 변환 */
    public Map<String, Integer> getHashAsIntMap(String key) {
        Map<String, String> raw = hashOps.entries(key);
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, String> entry : raw.entrySet()) {
            result.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        }
        return result;
    }

    /** ✅ 숫자형 값 증가 (공부시간) */
    public void incrementValue(String key, int delta) {
        valueOps.increment(key, delta);
    }

    /** ✅ prefix로 시작하는 키-값(숫자) 목록 반환 */
    public Map<String, Integer> scanByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        Map<String, Integer> result = new HashMap<>();
        for (String key : keys) {
            String val = valueOps.get(key);
            if (val != null) {
                result.put(key, Integer.parseInt(val));
            }
        }
        return result;
    }

    /** ✅ 방 관련 모든 Redis 로그 삭제 */
    public void deleteRoomLogs(Long roomId) {
        Set<String> keys = redisTemplate.keys("*:" + roomId + "*");
        redisTemplate.delete(keys);
    }
}
