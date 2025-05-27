// src/main/java/com/hamcam/back/service/team/TeamServiceImpl.java
package com.hamcam.back.service.team;

import com.hamcam.back.entity.team.Team;
import com.hamcam.back.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository repo;

    @Override
    public List<Team> getTeamsForUser(Long userId) {
        return repo.findByMembers_Id(userId);
    }
}
