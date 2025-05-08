package com.hamcam.back.entity.study;

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

    private String roomType;  // QUIZ / FOCUS

    private Integer maxParticipants;

    private String password;
}
