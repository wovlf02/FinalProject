package com.hamcam.back.repository.exam;

import com.hamcam.back.entity.exam.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    
    // 과목별 문제 조회
    List<Problem> findBySubject(String subject);

    List<Problem> findByProblemIdIn(List<Long> ids);
} 