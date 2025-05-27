package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    /** 특정 방의 발표 기록 조회 */
    List<Presentation> findAllByRoomId(Long roomId);
}
