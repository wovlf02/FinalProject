package com.hamcam.back.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /** ✅ 채팅 로그 삭제 */
    public void deleteChatLog(Long roomId) {
        String key = "chat:" + roomId;
        redisTemplate.delete(key);
    }

    /** ✅ 집중 시간 로그 삭제 (focus:roomId:* 전부 삭제) */
    public void deleteFocusRoomData(Long roomId) {
        Set<String> keys = redisTemplate.keys("focus:" + roomId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /** ✅ 집중 시간 증가 */
    public void increaseValue(String key, int increment) {
        redisTemplate.opsForValue().increment(key, increment);
    }

    /** ✅ 집중 시간 값 가져오기 */
    public int getIntValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return (value instanceof Integer) ? (Integer) value : Integer.parseInt(String.valueOf(value));
    }

    /** ✅ 집중 시간 직접 삭제 */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}
