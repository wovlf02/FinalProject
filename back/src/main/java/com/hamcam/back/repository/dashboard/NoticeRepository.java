package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.dashboard.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
