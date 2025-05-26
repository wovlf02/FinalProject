package com.hamcam.back.entity.study.team;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusRoom extends StudyRoom {

    private int goalMinutes;
    private boolean isFinished;

    private Long winnerUserId;
}
