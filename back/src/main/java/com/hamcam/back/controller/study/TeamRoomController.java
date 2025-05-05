package com.hamcam.back.controller.study;

import com.hamcam.back.dto.study.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.TeamRoomResponse;
import com.hamcam.back.service.study.TeamRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study/team/rooms")
public class TeamRoomController {

    @Autowired
    private TeamRoomService teamRoomService;

    @PostMapping("/create")
    public TeamRoomResponse create(@RequestBody TeamRoomCreateRequest request) {
        return teamRoomService.createTeamRoom(request);
    }

    @GetMapping("/{id}")
    public TeamRoomResponse getById(@PathVariable Long id) {
        return teamRoomService.getTeamRoomById(id);
    }
}

