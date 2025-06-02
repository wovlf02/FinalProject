package com.hamcam.back.service.dashboard;

import com.hamcam.back.dto.dashboard.reflection.request.OptionReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.RangeReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.WeeklyReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.response.ReflectionType;
import com.hamcam.back.dto.dashboard.reflection.response.WeeklyReflectionResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.StudySession;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.dashboard.StudySessionRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GPTReflectionService {

    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;

    public WeeklyReflectionResponse generateWeeklyReflection(WeeklyReflectionRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(
                user, request.getStartDate(), request.getEndDate());

        String prompt = buildPrompt(sessions, request.getStartDate(), request.getEndDate(), ReflectionType.GENERAL);
        String reflection = mockGpt(prompt);
        return WeeklyReflectionResponse.builder().reflectionText(reflection).build();
    }

    public WeeklyReflectionResponse generateReflectionByRange(RangeReflectionRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(
                user, request.getStartDate(), request.getEndDate());

        String prompt = buildPrompt(sessions, request.getStartDate(), request.getEndDate(), ReflectionType.GENERAL);
        String reflection = mockGpt(prompt);
        return WeeklyReflectionResponse.builder().reflectionText(reflection).build();
    }

    public WeeklyReflectionResponse generateCustomReflection(OptionReflectionRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(
                user, request.getStartDate(), request.getEndDate());

        String prompt = buildPrompt(sessions, request.getStartDate(), request.getEndDate(), request.getType());
        String reflection = mockGpt(prompt);
        return WeeklyReflectionResponse.builder().reflectionText(reflection).build();
    }

    private String buildPrompt(List<StudySession> sessions, LocalDate start, LocalDate end, ReflectionType type) {
        int totalMinutes = sessions.stream()
                .mapToInt(StudySession::getDurationMinutes)
                .sum();

        int avgFocus = (int) Math.round(
                sessions.stream()
                        .mapToDouble(StudySession::getFocusRate)
                        .average()
                        .orElse(0.0)
        );

        int avgAccuracy = (int) Math.round(
                sessions.stream()
                        .mapToDouble(StudySession::getAccuracy)
                        .average()
                        .orElse(0.0)
        );

        return switch (type) {
            case GENERAL -> String.format("""
            기간: %s ~ %s
            총 공부 시간: %d분
            평균 집중률: %d%%
            평균 정확도: %d%%

            위 데이터를 바탕으로 학습 회고를 작성해줘. 내용은 다음을 포함해야 해:
            - 전반적인 성과 평가
            - 잘한 점과 아쉬운 점
            - 다음 주를 위한 간단한 제안
            """, start, end, totalMinutes, avgFocus, avgAccuracy);

            case MOTIVATION -> String.format("""
            기간: %s ~ %s
            총 공부 시간: %d분
            평균 집중률: %d%%
            평균 정확도: %d%%

            위 데이터를 바탕으로 사용자가 학습 의욕을 가질 수 있도록 응원과 격려 중심의 회고를 작성해줘.
            """, start, end, totalMinutes, avgFocus, avgAccuracy);
        };
    }


    private String mockGpt(String prompt) {
        return "이번 주 학습에서 집중력이 일정하게 유지되었고, 정확도 또한 꾸준히 향상되었습니다. " +
                "특히 주 중반의 집중력이 돋보였으며, 전체적으로 우수한 학습 흐름을 보였습니다. " +
                "다음 주에도 루틴을 유지하면서 약점 복습에 시간을 투자해 보세요!";
    }

    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }
}
