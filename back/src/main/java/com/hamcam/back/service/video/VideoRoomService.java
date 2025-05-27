package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.request.*;
import com.hamcam.back.dto.video.response.VideoRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.video.VideoRoomRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [VideoRoomService]
 * WebRTC 기반 화상 학습방 관리 서비스 (세션 기반)
 */
@Service
@RequiredArgsConstructor
public class VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    private static final String USER_COUNT_KEY = "videoRoom:userCount:";

    /** ✅ 화상 채팅방 생성 */
    @Transactional
    public VideoRoomResponse createRoom(VideoRoomCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = getSessionUserId(httpRequest);
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        VideoRoom room = VideoRoom.builder()
                .teamId(request.getTeamId())
                .title(request.getTitle())
                .hostId(userId)
                .isActive(true)
                .build();

        VideoRoom saved = videoRoomRepository.save(room);
        return VideoRoomResponse.fromEntity(saved);
    }

    /** ✅ 팀 ID 기준 방 목록 조회 */
    public List<VideoRoomResponse> getRoomsByTeam(VideoRoomListRequest request) {
        return videoRoomRepository.findByTeamId(request.getTeamId()).stream()
                .map(VideoRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /** ✅ 단일 방 조회 */
    public VideoRoomResponse getRoomById(VideoRoomDetailRequest request) {
        VideoRoom room = videoRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.VIDEO_ROOM_NOT_FOUND));
        return VideoRoomResponse.fromEntity(room);
    }

    /** ✅ 입장 시 접속자 수 증가 (최초값 null 방지) */
    public void increaseUserCount(VideoRoomUserRequest request) {
        String key = USER_COUNT_KEY + request.getRoomId();
        redisTemplate.opsForValue().setIfAbsent(key, 0);
        redisTemplate.opsForValue().increment(key);
    }

    /** ✅ 퇴장 시 접속자 수 감소 (0 미만 방지) */
    public void decreaseUserCount(VideoRoomUserRequest request) {
        String key = USER_COUNT_KEY + request.getRoomId();
        Long current = getUserCount(request);
        if (current > 0) {
            redisTemplate.opsForValue().decrement(key);
        } else {
            redisTemplate.opsForValue().set(key, 0); // 혹시 모를 음수 방지
        }
    }

    /** ✅ 현재 접속자 수 조회 */
    public Long getUserCount(VideoRoomUserRequest request) {
        String key = USER_COUNT_KEY + request.getRoomId();
        Object count = redisTemplate.opsForValue().get(key);
        try {
            return count == null ? 0L : Long.parseLong(count.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /** ✅ 세션에서 사용자 ID 조회 */
    private Long getSessionUserId(HttpServletRequest request) {
        return SessionUtil.getUserId(request);
    }
}
