// src/main/java/com/hamcam/back/entity/team/Team.java
package com.hamcam.back.entity.team;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "team")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // (optional) 회원-팀 Join 테이블이 있다면 매핑
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
      name = "user_team",
      joinColumns = @JoinColumn(name = "team_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;
}
