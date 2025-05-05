package com.hamcam.back.service.study;

import com.hamcam.back.dto.study.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.TeamRoomResponse;
import com.hamcam.back.entity.study.TeamRoom;
import com.hamcam.back.repository.study.TeamRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamRoomService {

    @Autowired
    private TeamRoomRepository teamRoomRepository;

    public TeamRoomResponse createTeamRoom(TeamRoomCreateRequest request) {
        TeamRoom teamRoom = TeamRoom.builder()
                .title(request.getTitle())
                .roomType(request.getRoomType())
                .maxParticipants(request.getMaxParticipants())
                .password(request.getPassword())
                .build();

        TeamRoom saved = teamRoomRepository.save(teamRoom);

        return new TeamRoomResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getRoomType(),
                saved.getMaxParticipants(),
                saved.getPassword()
        );
    }

    public TeamRoomResponse getTeamRoomById(Long id) {
        TeamRoom room = teamRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamRoom not found"));

        return new TeamRoomResponse(
                room.getId(),
                room.getTitle(),
                room.getRoomType(),
                room.getMaxParticipants(),
                room.getPassword()
        );
    }
}
