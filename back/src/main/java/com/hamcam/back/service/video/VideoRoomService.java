package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.request.VideoRoomRequest;
import com.hamcam.back.dto.video.response.VideoRoomResponse;
import com.hamcam.back.entity.video.VideoRoom;
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

    /** 사용자 수 증가 */
    public void increaseUserCount(Long roomId) {
        redisTemplate.opsForValue().increment(USER_COUNT_KEY + roomId);
    }

    /** 사용자 수 감소 */
    public void decreaseUserCount(Long roomId) {
        redisTemplate.opsForValue().decrement(USER_COUNT_KEY + roomId);
    }

    /** 현재 사용자 수 조회 */
    public Long getUserCount(Long roomId) {
        Object count = redisTemplate.opsForValue().get(USER_COUNT_KEY + roomId);
        return count == null ? 0 : Long.parseLong(count.toString());
    }

    /** 화상채팅방 생성 */
    public VideoRoomResponse createRoom(VideoRoomRequest request) {
        VideoRoom room = VideoRoom.builder()
                .teamId(request.getTeamId())
                .title(request.getTitle())
                .isActive(true)
                .build();

        return VideoRoomResponse.fromEntity(videoRoomRepository.save(room));
    }

    /** 팀 ID 기준 방 목록 조회 */
    public List<VideoRoomResponse> getRoomsByTeam(Long teamId) {
        return videoRoomRepository.findByTeamId(teamId).stream()
                .map(VideoRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /** 방 ID 기준 단건 조회 */
    public VideoRoomResponse getRoomById(Long id) {
        return videoRoomRepository.findById(id)
                .map(VideoRoomResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("해당 화상 학습방을 찾을 수 없습니다."));
    }
}
