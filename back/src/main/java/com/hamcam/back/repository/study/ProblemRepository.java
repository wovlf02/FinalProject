package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ✅ 문제 조건 기반 조회용 리포지토리
 */
@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findBySubjectAndUnit_UnitAndCorrectRateBetween(String subject, String unitName, double min, double max);
}
