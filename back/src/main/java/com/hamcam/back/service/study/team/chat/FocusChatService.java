package com.hamcam.back.service.study.team.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.study.team.socket.response.FocusChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class FocusChatService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String FOCUS_CHAT_KEY_PREFIX = "focus:chat:";

    /**
     * ✅ 채팅 메시지 Redis 저장 및 응답 객체 생성
     *
     * @param roomId   집중방 ID
     * @param userId   전송자 ID
     * @param content  메시지 내용
     * @return FocusChatMessageResponse
     */
    public FocusChatMessageResponse saveAndBuild(Long roomId, Long userId, String content) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        FocusChatMessageResponse response = FocusChatMessageResponse.builder()
                .userId(userId)
                .nickname(sender.getNickname())
                .profileImageUrl(sender.getProfileImageUrl())
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build();

        try {
            String json = objectMapper.writeValueAsString(response);
            String redisKey = FOCUS_CHAT_KEY_PREFIX + roomId;

            redisTemplate.opsForList().rightPush(redisKey, json);
            redisTemplate.expire(redisKey, Duration.ofDays(1)); // TTL: 1일

            log.debug("✅ Redis 저장 완료 [key={}, content={}]", redisKey, content);
        } catch (JsonProcessingException e) {
            log.error("❌ Redis 저장 실패: JSON 직렬화 오류", e);
        }

        return response;
    }
}