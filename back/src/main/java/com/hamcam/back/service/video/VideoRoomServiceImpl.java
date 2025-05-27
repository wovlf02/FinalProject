package com.hamcam.back.service.video;

import com.hamcam.back.entity.video.RoomStatus;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.entity.video.VideoRoomParticipant;
import com.hamcam.back.repository.video.VideoRoomParticipantRepository;
import com.hamcam.back.repository.video.VideoRoomRepository;
import com.hamcam.back.entity.video.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class VideoRoomServiceImpl implements VideoRoomService {
    private final VideoRoomRepository roomRepo;
    private final VideoRoomParticipantRepository partRepo;

    @Override
    public VideoRoom createRoom(Long hostId, Long teamId, String title,
                                RoomType type, Integer maxParticipants,
                                String password, Integer targetTime) {
        VideoRoom room = VideoRoom.builder()
                .hostId(hostId)
                .teamId(teamId)
                .title(title)
                .type(type)
                .maxParticipants(maxParticipants)
                .password(password)
                .targetTime(targetTime)
                .status(RoomStatus.WAITING)
                .build();
        return roomRepo.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoRoom> getRoomsByTeam(Long teamId) {
        return roomRepo.findByTeamId(teamId);
    }

    @Override
    public void joinRoom(Integer roomId, Long userId) {
        if (!partRepo.existsByRoom_IdAndUserId(roomId, userId)) {
            VideoRoom room = roomRepo.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
            long count = partRepo.countByRoom_Id(roomId);
            if (count >= room.getMaxParticipants()) {
                throw new IllegalStateException("방이 가득 찼습니다.");
            }
            partRepo.save(new VideoRoomParticipant(room, userId));
        }
    }

    @Override
    public void leaveRoom(Integer roomId, Long userId) {
        partRepo.deleteByRoom_IdAndUserId(roomId, userId);
        long count = partRepo.countByRoom_Id(roomId);
        if (count == 0) {
            roomRepo.deleteById(roomId);
        }
    }

    @Override
    public Long getParticipantCount(Integer roomId) {
        return partRepo.countByRoom_Id(roomId);
    }

    // ✅ 추가: 참여자 ID 목록 조회 구현
    @Override
    public List<Long> getParticipants(Integer roomId) {
        return partRepo.findUserIdsByRoomId(roomId);
    }
}
