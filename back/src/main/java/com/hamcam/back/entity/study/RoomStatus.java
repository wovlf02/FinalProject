package com.hamcam.back.entity.study;

/**
 * [RoomStatus]
 * 팀 학습방의 현재 상태를 나타내는 열거형
 */
public enum RoomStatus {

    WAITING,            // 대기 중 (생성 후 참가자 모이는 상태)
    QUIZ_IN_PROGRESS,   // 문제풀이 중
    QUIZ_ENDED,         // 문제풀이 종료됨
    FOCUS_IN_PROGRESS,  // 공부시간 경쟁 중
    FOCUS_COMPLETE      // 목표시간 도달하여 종료됨

}
