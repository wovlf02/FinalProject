package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.Passage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ✅ 국어 지문 조회용 리포지토리
 */
@Repository
public interface PassageRepository extends JpaRepository<Passage, Long> {
}
