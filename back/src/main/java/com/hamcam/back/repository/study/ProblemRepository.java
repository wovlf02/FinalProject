package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findBySubjectAndSourceAndCorrectRateBetween(String subject, String source, double min, double max);
}
