package com.hamcam.back.repository.exam;

import com.hamcam.back.entity.exam.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByUserIdOrderByDateDesc(String userId);
    void deleteByUserId(String userId);
} 