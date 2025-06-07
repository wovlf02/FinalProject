package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {
    List<StudyLog> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);


}
