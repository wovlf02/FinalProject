package com.hamcam.back.service.study.personal;

import com.hamcam.back.dto.study.personal.request.StudySessionRequest;
import com.hamcam.back.entity.auth.User;
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
                .unitName(request.getUnitName())
                .durationMinutes(request.getDurationMinutes())
                .studyDate(LocalDate.now())
                .startedAt(LocalDateTime.now().minusMinutes(request.getDurationMinutes()))
                .endedAt(LocalDateTime.now())
                .focusRate(100) // 추후 개선 가능
                .accuracy(100)
                .correctRate(100)
                .studyType(StudyType.valueOf(request.getStudyType())) // "PERSONAL"
                .subject("자율") // 또는 "기타", 추후 단원명으로 대체 가능
                .build();

        studySessionRepository.save(session);
    }
}
