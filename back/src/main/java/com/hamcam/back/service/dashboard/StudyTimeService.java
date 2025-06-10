package com.hamcam.back.service.dashboard;

import com.hamcam.back.dto.dashboard.time.request.StudyTimeUpdateRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.StudyTime;
import com.hamcam.back.repository.dashboard.StudyTimeRepository;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StudyTimeService {

    private final StudyTimeRepository studyTimeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StudyTime getStudyTime(HttpServletRequest request) {
        try {
            Long userId = SessionUtil.getUserId(request);
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
            
            return studyTimeRepository.findByUser(user)
                .orElseGet(() -> createDefaultStudyTime(user));
        } catch (Exception e) {
            throw new RuntimeException("공부 시간 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Transactional
    public StudyTime updateStudyTime(StudyTimeUpdateRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = SessionUtil.getUserId(httpRequest);
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
            
            StudyTime studyTime = studyTimeRepository.findByUser(user)
                .orElseGet(() -> createDefaultStudyTime(user));

            // 기본값 설정
            Integer weeklyGoalMinutes = request.getWeeklyGoalMinutes() != null ? request.getWeeklyGoalMinutes() : 0;
            Integer todayGoalMinutes = request.getTodayGoalMinutes() != null ? request.getTodayGoalMinutes() : 0;
            Integer todayStudyMinutes = request.getTodayStudyMinutes() != null ? request.getTodayStudyMinutes() : 0;

            studyTime.updateGoals(weeklyGoalMinutes, todayGoalMinutes);
            studyTime.updateTodayStudyMinutes(todayStudyMinutes);
            
            return studyTimeRepository.save(studyTime);
        } catch (Exception e) {
            throw new RuntimeException("공부 시간 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private StudyTime createDefaultStudyTime(User user) {
        try {
            StudyTime studyTime = StudyTime.createDefault(user);
            return studyTimeRepository.save(studyTime);
        } catch (Exception e) {
            throw new RuntimeException("기본 공부 시간 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
} 