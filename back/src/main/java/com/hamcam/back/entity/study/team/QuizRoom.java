package com.hamcam.back.entity.study.team;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRoom extends StudyRoom {

    private String subject;
    private String grade;
    private String month;
    private String difficulty;

    private Long problemId;
    private boolean isOngoing;

    /** ✅ 투표 내역 (삭제 연동) */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoomVote> votes;
}
