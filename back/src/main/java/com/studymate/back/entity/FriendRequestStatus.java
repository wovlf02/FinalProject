package com.studymate.back.entity;

/**
 * FriendRequestStatus (친구 요청 상태 ENUM)
 * 친구 요청의 상태를 정의
 * PENDING: 대기 / ACCEPTED: 수락 / REJECTED: 거절
 */
public enum FriendRequestStatus {
    PENDING,    // 대기
    ACCEPTED,   // 수락
    REJECTED,   // 거절
}
