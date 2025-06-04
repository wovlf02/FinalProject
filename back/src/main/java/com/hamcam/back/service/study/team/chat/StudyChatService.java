package com.hamcam.back.service.study.team.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamcam.back.dto.study.team.socket.response.StudyChatMessageResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ✅ 문제풀이방 / 집중방 채팅 서비스
 * Redis에 채팅 저장 + WebSocket으로 브로드캐스트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudyChatService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String REDIS_CHAT_KEY_PATTERN = "study:%s:chat"; // e.g. study:focus-1:chat

    /**
     * ✅ 채팅 메시지 처리: Redis 저장 + WebSocket 브로드캐스트
     *
     * @param roomType "focus" 또는 "quiz"
     * @param roomId   방 ID
     * @param userId   발신자 ID
     * @param content  채팅 내용
     */
    public void handleAndBroadcastChatMessage(String roomType, Long roomId, Long userId, String content) {
        String roomKey = roomType + "-" + roomId;

        // ✅ 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // ✅ 메시지 DTO 생성
        StudyChatMessageResponse response = StudyChatMessageResponse.builder()
                .roomId(roomKey)
                .senderId(userId)
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImageUrl())
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();

        // ✅ Redis 저장
        String redisKey = String.format(REDIS_CHAT_KEY_PATTERN, roomKey);
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForList().rightPush(redisKey, json);
        } catch (Exception e) {
            log.error("❌ Redis 채팅 저장 실패 - key: {}, message: {}", redisKey, e.getMessage(), e);
        }

        // ✅ WebSocket 전송
        String topic = String.format("/sub/%s/room/%d", roomType, roomId);
        messagingTemplate.convertAndSend(topic, response);

        log.info("💬 채팅 전송 [{}] {}: {}", roomKey, user.getNickname(), content);
    }

}
