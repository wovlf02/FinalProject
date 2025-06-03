package com.hamcam.back.repository.community.study;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.SidebarStudy;
import com.hamcam.back.entity.community.StudyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {

    /** 특정 스터디에 참여한 전체 유저 목록 조회 */
    List<StudyParticipant> findByStudy(SidebarStudy study);

    /** 특정 스터디에 해당 유저가 참여 중인지 여부 확인 */
    boolean existsByStudyAndUser(SidebarStudy study, User user);
}
