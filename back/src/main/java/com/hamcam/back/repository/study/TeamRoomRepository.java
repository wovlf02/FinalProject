package com.hamcam.back.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * [TeamRoomRepository]
 *
 * 팀 학습방(TeamRoom) 관련 JPA Repository입니다.
 * - 방 생성, 조회, 삭제 등을 처리합니다.
 * - 추후 목표 시간, 생성자, 방 이름 등 조건 기반 조회 확장 가능
 */
@Repository
public interface TeamRoomRepository extends JpaRepository<TeamRoom, Long> {

    // 🔧 예: 추후 확장용 메서드 예시
    // List<TeamRoom> findByCreatedBy(User user);
    // List<TeamRoom> findByGoalMinutesGreaterThan(int minutes);
    // Optional<TeamRoom> findByCode(String inviteCode);
}
