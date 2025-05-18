package com.hamcam.back.entity.study;

import com.hamcam.back.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String roomType; // QUIZ / FOCUS

    private Integer maxParticipants;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host; // ✅ 방장 (현재 로그인한 사용자)
}
