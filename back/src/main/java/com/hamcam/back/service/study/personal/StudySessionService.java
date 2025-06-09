package com.hamcam.back.service.study.personal;

import com.hamcam.back.dto.study.personal.request.StudySessionRequest;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.StudySession;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.dashboard.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [StudySessionService]
 * 개인 공부 기록 저장 서비스
 */
@Service
@RequiredArgsConstructor
public class StudySessionService {

    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;

    /**
     * ✅ 개인 공부 기록 저장
     */
    public void saveSession(Long userId, StudySessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        StudySession session = StudySession.builder()
                .user(user)
                .studyDate(LocalDate.now())
                .totalMinutes(request.getDurationMinutes())
                .focusMinutes(request.getDurationMinutes())  // 전체를 집중했다고 가정
                .focusRate(100.0)
                .accuracy(100.0)
                .correctRate(100.0)
                .subject(request.getSubject() != null ? request.getSubject() : "자율")
                .build();

        studySessionRepository.save(session);
    }

}
