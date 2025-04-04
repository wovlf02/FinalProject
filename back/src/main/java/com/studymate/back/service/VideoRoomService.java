package com.studymate.back.service;

import com.studymate.back.dto.VideoRoomResponse;
import com.studymate.back.entity.VideoRoom;
import com.studymate.back.repository.VideoRoomRepository;
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
