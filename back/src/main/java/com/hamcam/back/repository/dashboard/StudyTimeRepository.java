package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyTimeRepository extends JpaRepository<StudyTime, Long> {
    Optional<StudyTime> findByUser(User user);
}
