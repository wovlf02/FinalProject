package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.VideoRoomRequest;
import com.hamcam.back.dto.video.VideoRoomResponse;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.repository.video.VideoRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_COUNT_KEY = "videoRoom:userCount:";

    // Redis: 사용자 수 증가
    public void increaseUserCount(Long roomId) {
        redisTemplate.opsForValue().increment(USER_COUNT_KEY + roomId);
    }

    // Redis: 사용자 수 감소
    public void decreaseUserCount(Long roomId) {
        redisTemplate.opsForValue().decrement(USER_COUNT_KEY + roomId);
    }

    // Redis: 사용자 수 조회
    public Long getUserCount(Long roomId) {
        Object count = redisTemplate.opsForValue().get(USER_COUNT_KEY + roomId);
        return count == null ? 0 : Long.parseLong(count.toString());
    }

    // DB: 화상채팅방 생성
    public VideoRoomResponse createRoom(VideoRoomRequest request) {
        VideoRoom room = new VideoRoom();
        room.setTeamId(request.getTeamId());
        room.setTitle(request.getTitle());
        room.setIsActive(true);

        VideoRoom saved = videoRoomRepository.save(room);
        return new VideoRoomResponse(saved.getId(), saved.getTeamId(), saved.getTitle(), saved.getIsActive(), saved.getCreatedAt());
    }

    // DB: 팀별 방 목록 조회
    public List<VideoRoomResponse> getRoomsByTeam(Long teamId) {
        return videoRoomRepository.findByTeamId(teamId)
                .stream()
                .map(r -> new VideoRoomResponse(r.getId(), r.getTeamId(), r.getTitle(), r.getIsActive(), r.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // DB: 방 ID로 단일 조회
    public VideoRoomResponse getRoomById(Long id) {
        VideoRoom room = videoRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 방이 존재하지 않습니다."));
        return new VideoRoomResponse(room.getId(), room.getTeamId(), room.getTitle(), room.getIsActive(), room.getCreatedAt());
    }
}
