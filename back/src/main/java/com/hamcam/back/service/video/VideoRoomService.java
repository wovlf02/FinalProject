package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.VideoRoomRequest;
import com.hamcam.back.dto.video.VideoRoomResponse;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.video.VideoRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [VideoRoomService]
 *
 * WebRTC 기반 화상 학습방 관리 서비스
 * - Redis로 접속자 수 추적
 * - MySQL로 방 생성 및 조회
 */
@Service
@RequiredArgsConstructor
public class VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_COUNT_KEY = "videoRoom:userCount:";

    /**
     * Redis: 사용자 수 증가
     */
    public void increaseUserCount(Long roomId) {
        redisTemplate.opsForValue().increment(USER_COUNT_KEY + roomId);
    }

    /**
     * Redis: 사용자 수 감소
     */
    public void decreaseUserCount(Long roomId) {
        redisTemplate.opsForValue().decrement(USER_COUNT_KEY + roomId);
    }

    /**
     * Redis: 현재 사용자 수 조회
     */
    public Long getUserCount(Long roomId) {
        Object count = redisTemplate.opsForValue().get(USER_COUNT_KEY + roomId);
        return count == null ? 0 : Long.parseLong(count.toString());
    }

    /**
     * DB: 화상채팅방 생성
     */
    public VideoRoomResponse createRoom(VideoRoomRequest request) {
        VideoRoom room = VideoRoom.builder()
                .teamId(request.getTeamId())
                .title(request.getTitle())
                .isActive(true)
                .build();

        VideoRoom saved = videoRoomRepository.save(room);
        return VideoRoomResponse.fromEntity(saved);
    }

    /**
     * DB: 팀 ID 기준 방 목록 조회
     */
    public List<VideoRoomResponse> getRoomsByTeam(Long teamId) {
        return videoRoomRepository.findByTeamId(teamId).stream()
                .map(VideoRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * DB: 방 ID 기준 단건 조회
     */
    public VideoRoomResponse getRoomById(Long id) {
        VideoRoom room = videoRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.VIDEO_ROOM_NOT_FOUND));
        return VideoRoomResponse.fromEntity(room);
    }
}
