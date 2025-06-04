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

    /**
     * ✅ 과목, 출처, 정답률 범위 조건에 맞는 문제 리스트 조회
     *
     * @param subject 과목명 (국어, 수학, 영어 등)
     * @param source 출처 (예: "2025년 수능")
     * @param min 최소 정답률
     * @param max 최대 정답률
     * @return 조건에 맞는 문제 리스트
     */
    List<Problem> findBySubjectAndSourceAndCorrectRateBetween(
            String subject,
            String source,
            Double min,
            Double max
    );
}
