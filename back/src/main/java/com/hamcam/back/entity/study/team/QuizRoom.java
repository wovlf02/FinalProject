package com.hamcam.back.entity.study.team;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("QuizRoom") // ✅ 상속 전략 구분자
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

    /** ✅ StudyRoom에서 선언된 추상 메서드 오버라이드 */
    @Override
    public RoomType getRoomType() {
        return RoomType.QUIZ;
    }
}
