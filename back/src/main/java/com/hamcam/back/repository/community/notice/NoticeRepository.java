package com.hamcam.back.repository.community.notice;

import com.hamcam.back.entity.community.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * [공지사항 Repository]
 * - 주요 공지사항 3건 (등록일 기준)
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findTop3ByOrderByCreatedAtDesc();

    List<Notice> findAllByOrderByCreatedAtDesc();  // 최신순 전체 조회
}
