package com.hamcam.back.controller.study;

import com.hamcam.back.dto.study.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.TeamRoomResponse;
import com.hamcam.back.service.study.TeamRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [TeamRoomController]
 * 팀 기반 스터디 방 생성 및 조회 API 컨트롤러
 */
@RestController
@RequestMapping("/api/study/team/rooms")
@RequiredArgsConstructor
public class TeamRoomController {

    private final TeamRoomService teamRoomService;

    /**
     * [스터디 방 생성]
     *
     * @param request 스터디 방 생성 요청 DTO
     * @return 생성된 스터디 방 정보
     */
    @PostMapping("/create")
    public ResponseEntity<TeamRoomResponse> create(@RequestBody TeamRoomCreateRequest request) {
        TeamRoomResponse response = teamRoomService.createTeamRoom(request);
        return ResponseEntity.ok(response);
    }

    /**
     * [스터디 방 단건 조회]
     *
     * @param id 팀방 ID
     * @return 팀방 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamRoomResponse> getById(@PathVariable Long id) {
        TeamRoomResponse response = teamRoomService.getTeamRoomById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * [전체 팀 스터디 방 목록 조회]
     *
     * @return 전체 스터디 방 리스트
     */
    @GetMapping
    public ResponseEntity<List<TeamRoomResponse>> getAllRooms() {
        List<TeamRoomResponse> response = teamRoomService.getAllTeamRooms();
        return ResponseEntity.ok(response);
    }
}
