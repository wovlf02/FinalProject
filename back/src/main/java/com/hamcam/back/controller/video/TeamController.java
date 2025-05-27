// src/main/java/com/hamcam/back/controller/team/TeamController.java
package com.hamcam.back.controller.team;

import com.hamcam.back.dto.team.TeamResponse;
import com.hamcam.back.entity.team.Team;
import com.hamcam.back.service.team.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService svc;

    /** 현재 로그인된 유저가 속한 팀 목록 조회 **/
    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> getMyTeams(HttpServletRequest request) {
        // AuthFilter 등에서 request.setAttribute("userId", ..) 해 두셨다고 가정
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Team> teams = svc.getTeamsForUser(userId);
        List<TeamResponse> dto = teams.stream()
            .map(TeamResponse::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dto);
    }
}
