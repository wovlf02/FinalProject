package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.VideoRoomResponse;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.repository.video.VideoRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;

    public VideoRoomResponse createRoom(String title) {
        VideoRoom room = new VideoRoom();
        room.setTitle(title);
        VideoRoom savedRoom = videoRoomRepository.save(room);
        return new VideoRoomResponse(savedRoom.getId(), savedRoom.getTitle());
    }

    public List<VideoRoomResponse> getAllRooms() {
        return videoRoomRepository.findAll().stream()
                .map(room -> new VideoRoomResponse(room.getId(), room.getTitle()))
                .collect(Collectors.toList());
    }
}
