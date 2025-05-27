// src/main/java/com/hamcam/back/dto/team/TeamResponse.java
package com.hamcam.back.dto.team;

import com.hamcam.back.entity.team.Team;
import lombok.Getter;

@Getter
public class TeamResponse {
    private final Long id;
    private final String name;

    public TeamResponse(Team t) {
        this.id = t.getId();
        this.name = t.getName();
    }
}
