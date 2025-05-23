package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.request.*;
import com.hamcam.back.dto.video.response.VideoRoomResponse;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.video.VideoRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [VideoRoomService]
 * WebRTC 기반 화상 학습방 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_COUNT_KEY = "videoRoom:userCount:";

    /** ✅ 화상 채팅방 생성 */
    public VideoRoomResponse createRoom(VideoRoomCreateRequest request) {
        VideoRoom room = VideoRoom.builder()
                .teamId(request.getTeamId())
                .title(request.getTitle())
                .hostId(request.getUserId())
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

    /** ✅ 입장 시 접속자 수 증가 */
    public void increaseUserCount(VideoRoomUserRequest request) {
        redisTemplate.opsForValue().increment(USER_COUNT_KEY + request.getRoomId());
    }

    /** ✅ 퇴장 시 접속자 수 감소 */
    public void decreaseUserCount(VideoRoomUserRequest request) {
        redisTemplate.opsForValue().decrement(USER_COUNT_KEY + request.getRoomId());
    }

    /** ✅ 현재 접속자 수 조회 */
    public Long getUserCount(VideoRoomUserRequest request) {
        Object count = redisTemplate.opsForValue().get(USER_COUNT_KEY + request.getRoomId());
        return count == null ? 0L : Long.parseLong(count.toString());
    }
}
