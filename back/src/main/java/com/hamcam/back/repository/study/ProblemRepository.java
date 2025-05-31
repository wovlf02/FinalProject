package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

}
