// src/main/java/com/hamcam/back/service/team/TeamService.java
package com.hamcam.back.service.team;

import com.hamcam.back.entity.team.Team;

import java.util.List;

public interface TeamService {
    List<Team> getTeamsForUser(Long userId);
}
