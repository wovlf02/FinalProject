package com.hamcam.back.service.study;

import com.hamcam.back.dto.study.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.TeamRoomResponse;
import com.hamcam.back.entity.study.TeamRoom;
import com.hamcam.back.repository.study.TeamRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamRoomService {

    @Autowired
    private TeamRoomRepository teamRoomRepository;

    public TeamRoomResponse createTeamRoom(TeamRoomCreateRequest request) {
        TeamRoom room = new TeamRoom();
        room.setTitle(request.getTitle());
        room.setRoomType(request.getRoomType());
        room.setMaxParticipants(request.getMaxParticipants());
        room.setPassword(request.getPassword());

        TeamRoom saved = teamRoomRepository.save(room);
        return TeamRoomResponse.from(saved);
    }

    public TeamRoomResponse getTeamRoomById(Long id) {
        TeamRoom room = teamRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 학습방을 찾을 수 없습니다."));
        return TeamRoomResponse.from(room);
    }

    // ✅ 전체 학습방 목록 반환
    public List<TeamRoomResponse> getAllTeamRooms() {
        List<TeamRoom> rooms = teamRoomRepository.findAll();
        return rooms.stream()
                .map(TeamRoomResponse::from)
                .collect(Collectors.toList());
    }
}
