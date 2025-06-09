package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.Problem;
import com.hamcam.back.entity.study.team.QuizRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRoomRepository extends JpaRepository<QuizRoom, Long> {

    @Query("SELECT r.currentProblem FROM QuizRoom r WHERE r.id = :roomId")
    Problem findCurrentProblemByRoomId(@Param("roomId") Long roomId);


}
