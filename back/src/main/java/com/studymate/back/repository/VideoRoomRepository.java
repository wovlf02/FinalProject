package com.studymate.back.repository;

import com.studymate.back.entity.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {
}
