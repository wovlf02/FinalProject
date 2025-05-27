// src/main/java/com/hamcam/back/repository/team/TeamRepository.java
package com.hamcam.back.repository.team;

import com.hamcam.back.entity.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // 로그인된 userId로 소속 팀 조회
    List<Team> findByMembers_Id(Long userId);
}
