package com.hamcam.back.service.dashboard;

import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.todo.request.TodoRequest;
import com.hamcam.back.dto.dashboard.todo.request.TodoUpdateRequest;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.*;
import com.hamcam.back.global.security.SecurityUtil;
import com.hamcam.back.repository.dashboard.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final TodoRepository todoRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final StudySessionRepository studySessionRepository;
    private final GoalRepository goalRepository;
    private final SecurityUtil securityUtil;

    // 📆 1. 월별 캘린더 이벤트
    public List<CalendarEventDto> getMonthlyCalendarEvents(YearMonth month) {
        User user = securityUtil.getCurrentUser();
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<Todo> todos = todoRepository.findAllByUserAndTodoDateBetween(user, start, end);
        List<ExamSchedule> exams = examScheduleRepository.findAllByUserOrderByExamDateAsc(user)
                .stream().filter(e -> !e.getExamDate().isBefore(start) && !e.getExamDate().isAfter(end)).toList();
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, end);

        Map<LocalDate, CalendarEventDto> map = new HashMap<>();
        todos.forEach(todo -> map.computeIfAbsent(todo.getTodoDate(), d -> new CalendarEventDto(d, new ArrayList<>(), new ArrayList<>(), 0))
                .getTodos().add(todo.getTitle()));
        exams.forEach(exam -> map.computeIfAbsent(exam.getExamDate(), d -> new CalendarEventDto(d, new ArrayList<>(), new ArrayList<>(), 0))
                .getExams().add(exam.getTitle()));
        sessions.forEach(s -> map.computeIfAbsent(s.getStudyDate(), d -> new CalendarEventDto(d, new ArrayList<>(), new ArrayList<>(), 0))
                .setTotalStudyMinutes(map.getOrDefault(s.getStudyDate(), new CalendarEventDto()).getTotalStudyMinutes() + s.getDurationMinutes()));

        return map.values().stream()
                .sorted(Comparator.comparing(CalendarEventDto::getDate))
                .collect(Collectors.toList());
    }

    // 📆 2. 특정 날짜의 Todo 목록
    public List<TodoResponse> getTodosByDate(LocalDate date) {
        User user = securityUtil.getCurrentUser();
        return todoRepository.findAllByUserAndTodoDateOrderByPriorityDesc(user, date)
                .stream().map(this::toTodoResponse).collect(Collectors.toList());
    }

    // 📆 3. Todo 생성
    public void createTodo(TodoRequest request) {
        User user = securityUtil.getCurrentUser();
        Todo todo = Todo.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .todoDate(request.getTodoDate())
                .priority(request.getPriority())
                .isCompleted(false)
                .build();
        todoRepository.save(todo);
    }

    // 📆 4. Todo 수정
    public void updateTodo(Long todoId, TodoUpdateRequest request) {
        Todo todo = getTodoOrThrow(todoId);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setTodoDate(request.getTodoDate());
        todo.setPriority(request.getPriority());
    }

    // 📆 5. Todo 삭제
    public void deleteTodo(Long todoId) {
        Todo todo = getTodoOrThrow(todoId);
        todoRepository.delete(todo);
    }

    // 📆 6. Todo 완료 체크
    public void completeTodo(Long todoId) {
        Todo todo = getTodoOrThrow(todoId);
        todo.setCompleted(true);
    }

    // 📅 7. 전체 시험 일정
    public List<ExamScheduleResponse> getAllExamSchedules() {
        User user = securityUtil.getCurrentUser();
        return examScheduleRepository.findAllByUserOrderByExamDateAsc(user)
                .stream().map(e -> ExamScheduleResponse.builder()
                        .id(e.getId())
                        .title(e.getTitle())
                        .examDate(e.getExamDate())
                        .build()).collect(Collectors.toList());
    }

    // 📅 8. 시험 일정 등록
    public void createExamSchedule(ExamScheduleRequest request) {
        User user = securityUtil.getCurrentUser();
        examScheduleRepository.save(ExamSchedule.builder()
                .user(user)
                .title(request.getTitle())
                .examDate(request.getExamDate())
                .build());
    }

    // 📅 9. 가장 가까운 시험 일정
    public DDayInfoResponse getNearestExamSchedule() {
        User user = securityUtil.getCurrentUser();
        return examScheduleRepository.findNearestExamSchedule(user, LocalDate.now())
                .map(e -> {
                    long diff = LocalDate.now().until(e.getExamDate()).getDays();
                    String dday = diff == 0 ? "D-day" : (diff > 0 ? "D-" + diff : "D+" + Math.abs(diff));
                    return DDayInfoResponse.builder()
                            .title(e.getTitle())
                            .examDate(e.getExamDate())
                            .ddayText(dday)
                            .build();
                }).orElse(null);
    }

    // 📊 10. 전체 학습 통계
    public TotalStatsResponse getTotalStudyStats() {
        User user = securityUtil.getCurrentUser();
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, LocalDate.of(2000,1,1), LocalDate.now());
        return TotalStatsResponse.builder()
                .totalStudyMinutes(sessions.stream().mapToInt(StudySession::getDurationMinutes).sum())
                .averageFocusRate((int) sessions.stream().mapToInt(StudySession::getFocusRate).average().orElse(0))
                .averageAccuracy((int) sessions.stream().mapToInt(StudySession::getAccuracy).average().orElse(0))
                .build();
    }

    // 📊 11. 과목별 통계 - 과목 연관 필드 없을 경우 생략

    // 📊 12. 주간 통계
    public WeeklyStatsResponse getWeeklyStats() {
        User user = securityUtil.getCurrentUser();
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(6);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, now);

        Map<LocalDate, WeeklyStatsResponse.DailyStat> map = new TreeMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            map.put(date, WeeklyStatsResponse.DailyStat.builder()
                    .date(date)
                    .studyMinutes(0)
                    .warningCount(0) // 추후 경고 시스템 도입 시 수정
                    .build());
        }
        for (StudySession session : sessions) {
            WeeklyStatsResponse.DailyStat stat = map.get(session.getStudyDate());
            stat.setStudyMinutes(stat.getStudyMinutes() + session.getDurationMinutes());
        }

        return WeeklyStatsResponse.builder()
                .dailyStats(new ArrayList<>(map.values()))
                .build();
    }

    // 📊 13. 월간 통계
    public MonthlyStatsResponse getMonthlyStats() {
        User user = securityUtil.getCurrentUser();
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, end);

        int total = sessions.stream().mapToInt(StudySession::getDurationMinutes).sum();
        Map<Integer, List<StudySession>> byWeek = sessions.stream()
                .collect(Collectors.groupingBy(s -> (s.getStudyDate().getDayOfMonth() - 1) / 7));

        List<Integer> weeklyAvgFocus = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<StudySession> weekSessions = byWeek.getOrDefault(i, List.of());
            int avg = (int) weekSessions.stream().mapToInt(StudySession::getFocusRate).average().orElse(0);
            weeklyAvgFocus.add(avg);
        }

        return MonthlyStatsResponse.builder()
                .totalStudyMinutes(total)
                .weeklyAverageFocusRates(weeklyAvgFocus)
                .build();
    }

    // 📊 14. 최고 집중일
    public BestFocusDayResponse getBestFocusDay() {
        User user = securityUtil.getCurrentUser();
        LocalDate start = LocalDate.now().minusDays(30);
        return studySessionRepository.findTopFocusDay(user, start).stream().findFirst()
                .map(s -> BestFocusDayResponse.builder()
                        .bestDay(s.getStudyDate())
                        .bestFocusRate(s.getFocusRate())
                        .build())
                .orElse(null);
    }

    // 🏁 15. GPT 목표 제안 - 추후 GPT 연동 또는 임시 로직 사용
    public GoalSuggestionResponse getSuggestedGoal() {
        // Mock 로직
        return GoalSuggestionResponse.builder()
                .message("최근 집중률을 고려해 하루 2.5시간을 추천합니다.")
                .suggestedDailyGoalMinutes(150)
                .build();
    }

    // 🏁 16. 수동 목표 수정
    public void updateGoalManually(GoalUpdateRequest request) {
        User user = securityUtil.getCurrentUser();
        goalRepository.save(Goal.builder()
                .user(user)
                .dailyGoalMinutes(request.getDailyGoalMinutes())
                .isSuggested(false)
                .setAt(LocalDate.now().atStartOfDay())
                .build());
    }

    // ===== 내부 유틸 =====
    private Todo getTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일입니다."));
    }

    private TodoResponse toTodoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .todoDate(todo.getTodoDate())
                .priority(todo.getPriority())
                .isCompleted(todo.isCompleted())
                .build();
    }

}
