package com.hamcam.back.entity.study.team;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * ✅ 팀 학습방 유형 구분 ENUM
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RoomType {
    QUIZ,   // 문제풀이방
    FOCUS   // 공부시간 경쟁방
}
