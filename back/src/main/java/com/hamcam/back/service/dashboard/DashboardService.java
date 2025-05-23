package com.hamcam.back.service.dashboard;

import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.calendar.request.CalendarRequest;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.exam.request.ExamUserRequest;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.goal.request.GoalUserRequest;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.dashboard.stats.request.StatsUserRequest;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.todo.request.*;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.*;
import com.hamcam.back.repository.auth.UserRepository;
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
    private final UserRepository userRepository;

    public List<CalendarEventDto> getMonthlyCalendarEvents(CalendarRequest request) {
        User user = getUser(request.getUserId());
        LocalDate start = request.getMonth().atDay(1);
        LocalDate end = request.getMonth().atEndOfMonth();

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

    public List<TodoResponse> getTodosByDate(TodoDateRequest request) {
        User user = getUser(request.getUserId());
        return todoRepository.findAllByUserAndTodoDateOrderByPriorityDesc(user, request.getDate())
                .stream().map(this::toTodoResponse).collect(Collectors.toList());
    }

    public TodoResponse createTodo(TodoRequest request) {
        User user = getUser(request.getUserId());

        Todo todo = Todo.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .todoDate(request.getDate())
                .priority(request.getPriority())
                .completed(false)
                .build();

        return toTodoResponse(todoRepository.save(todo));
    }

    public void updateTodo(TodoUpdateRequest request) {
        Todo todo = getTodoOrThrow(request.getTodoId());
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setTodoDate(request.getTodoDate());
        todo.setPriority(request.getPriority());
    }

    public void deleteTodo(TodoDeleteRequest request) {
        Todo todo = getTodoOrThrow(request.getTodoId());
        todoRepository.delete(todo);
    }

    public TodoResponse toggleTodoCompletion(TodoToggleRequest request) {
        Todo todo = getTodoOrThrow(request.getTodoId());
        todo.setCompleted(!todo.isCompleted());
        return toTodoResponse(todo);
    }

    public List<ExamScheduleResponse> getAllExamSchedules(ExamUserRequest request) {
        User user = getUser(request.getUserId());
        return examScheduleRepository.findAllByUserOrderByExamDateAsc(user)
                .stream().map(e -> ExamScheduleResponse.builder()
                        .id(e.getId())
                        .title(e.getTitle())
                        .examDate(e.getExamDate())
                        .build()).collect(Collectors.toList());
    }

    public void createExamSchedule(ExamScheduleRequest request) {
        User user = getUser(request.getUserId());
        examScheduleRepository.save(ExamSchedule.builder()
                .user(user)
                .title(request.getTitle())
                .examDate(request.getExamDate())
                .build());
    }

    public DDayInfoResponse getNearestExamSchedule(ExamUserRequest request) {
        User user = getUser(request.getUserId());
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

    public TotalStatsResponse getTotalStudyStats(StatsUserRequest request) {
        User user = getUser(request.getUserId());
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, LocalDate.of(2000, 1, 1), LocalDate.now());
        return TotalStatsResponse.builder()
                .totalStudyMinutes(sessions.stream().mapToInt(StudySession::getDurationMinutes).sum())
                .averageFocusRate((int) sessions.stream().mapToInt(StudySession::getFocusRate).average().orElse(0))
                .averageAccuracy((int) sessions.stream().mapToInt(StudySession::getAccuracy).average().orElse(0))
                .build();
    }

    public WeeklyStatsResponse getWeeklyStats(StatsUserRequest request) {
        User user = getUser(request.getUserId());
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(6);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, now);

        Map<LocalDate, WeeklyStatsResponse.DailyStat> map = new TreeMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            map.put(date, WeeklyStatsResponse.DailyStat.builder()
                    .date(date)
                    .studyMinutes(0)
                    .warningCount(0)
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

    public MonthlyStatsResponse getMonthlyStats(StatsUserRequest request) {
        User user = getUser(request.getUserId());
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

    public BestFocusDayResponse getBestFocusDay(StatsUserRequest request) {
        User user = getUser(request.getUserId());
        LocalDate start = LocalDate.now().minusDays(30);
        return studySessionRepository.findTopFocusDay(user, start).stream().findFirst()
                .map(s -> BestFocusDayResponse.builder()
                        .bestDay(s.getStudyDate())
                        .bestFocusRate(s.getFocusRate())
                        .build())
                .orElse(null);
    }

    public GoalSuggestionResponse getSuggestedGoal(GoalUserRequest request) {
        getUser(request.getUserId()); // 유효성 확인용
        return GoalSuggestionResponse.builder()
                .message("최근 집중률을 고려해 하루 2.5시간을 추천합니다.")
                .suggestedDailyGoalMinutes(150)
                .build();
    }

    public void updateGoalManually(GoalUpdateRequest request) {
        User user = getUser(request.getUserId());
        goalRepository.save(Goal.builder()
                .user(user)
                .dailyGoalMinutes(request.getDailyGoalMinutes())
                .isSuggested(false)
                .setAt(LocalDate.now().atStartOfDay())
                .build());
    }

    public List<SubjectStatsResponse> getSubjectStats(StatsUserRequest request) {
        User user = getUser(request.getUserId());

        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(
                user, LocalDate.of(2000, 1, 1), LocalDate.now());

        Map<String, List<StudySession>> bySubject = sessions.stream()
                .filter(s -> s.getSubject() != null)
                .collect(Collectors.groupingBy(StudySession::getSubject));

        return bySubject.entrySet().stream()
                .map(entry -> {
                    String subject = entry.getKey();
                    List<StudySession> subjectSessions = entry.getValue();
                    int totalFocus = subjectSessions.stream().mapToInt(StudySession::getDurationMinutes).sum();
                    int avgAccuracy = (int) subjectSessions.stream().mapToInt(StudySession::getAccuracy).average().orElse(0);
                    int avgCorrectRate = (int) subjectSessions.stream().mapToInt(StudySession::getCorrectRate).average().orElse(0);
                    return SubjectStatsResponse.builder()
                            .subjectName(subject)
                            .totalFocusMinutes(totalFocus)
                            .averageAccuracy(avgAccuracy)
                            .averageCorrectRate(avgCorrectRate)
                            .build();
                })
                .collect(Collectors.toList());
    }


    // ===== 내부 유틸 =====

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }

    private Todo getTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일입니다."));
    }

    private TodoResponse toTodoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .date(todo.getTodoDate())
                .priority(todo.getPriority())
                .completed(todo.isCompleted())
                .build();
    }
}
