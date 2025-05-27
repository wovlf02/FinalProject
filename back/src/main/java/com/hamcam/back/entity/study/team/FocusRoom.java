package com.hamcam.back.entity.study.team;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("FocusRoom") // ✅ JOINED 전략용 구분자
public class FocusRoom extends StudyRoom {

    private int goalMinutes;
    private boolean isFinished;

    private Long winnerUserId;

    /** ✅ StudyRoom에서 선언된 추상 메서드 구현 */
    @Override
    public RoomType getRoomType() {
        return RoomType.FOCUS;
    }
}
