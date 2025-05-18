package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * [VideoRoomRepository]
 *
 * WebRTC 영상 학습방(VideoRoom) 관련 JPA Repository입니다.
 * - TeamRoom과 연결된 영상방을 조회합니다.
 * - 추후 상태 기반 조회(활성/종료), 생성일 순 정렬 등 확장 가능합니다.
 */
@Repository
public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {

    /**
     * [팀 학습방 ID 기준 영상방 조회]
     * 특정 TeamRoom에 연결된 영상방 목록을 반환합니다.
     *
     * @param teamId 팀 학습방 ID
     * @return 연결된 VideoRoom 리스트
     */
    List<VideoRoom> findByTeamId(Long teamId);

    // 🔧 예: 추후 확장 예시
    // Optional<VideoRoom> findByRoomCode(String code);
    // List<VideoRoom> findByIsActiveTrue();
}
