package com.hamcam.back.service.dashboard;

import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.calendar.request.CalendarRequest;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.dashboard.notice.response.NoticeResponse;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.time.request.StudyTimeUpdateRequest;
import com.hamcam.back.dto.dashboard.todo.request.*;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.*;
import com.hamcam.back.entity.study.StudySession;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.dashboard.*;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hamcam.back.global.exception.ErrorCode;

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
    private final StudyTimeRepository studyTimeRepository;
    private final NoticeRepository noticeRepository;

    public List<CalendarEventDto> getMonthlyCalendarEvents(CalendarRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate start = request.getMonth().atDay(1);
        LocalDate end = request.getMonth().atEndOfMonth();

        List<Todo> todos = todoRepository.findAllByUserAndTodoDateBetween(user, start, end);
        List<ExamSchedule> exams = examScheduleRepository.findAllByUserOrderByExamDateAsc(user)
                .stream().filter(e -> !e.getExamDate().isBefore(start) && !e.getExamDate().isAfter(end)).toList();
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, end);

        Map<LocalDate, CalendarEventDto> map = new HashMap<>();
        todos.forEach(todo -> map.computeIfAbsent(todo.getTodoDate(), d -> new CalendarEventDto(d)).getTodos().add(todo.getTitle()));
        exams.forEach(exam -> map.computeIfAbsent(exam.getExamDate(), d -> new CalendarEventDto(d)).getExams().add(exam.getExamName()));
        sessions.forEach(s -> map.computeIfAbsent(s.getStudyDate(), d -> new CalendarEventDto(d)).setTotalStudyMinutes(
                map.getOrDefault(s.getStudyDate(), new CalendarEventDto(s.getStudyDate())).getTotalStudyMinutes() + s.getDurationMinutes()));

        return map.values().stream().sorted(Comparator.comparing(CalendarEventDto::getDate)).collect(Collectors.toList());
    }

    public List<TodoResponse> getTodosByDate(TodoDateRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        return todoRepository.findAllByUserAndTodoDateOrderByPriorityDesc(user, request.getDate())
                .stream().map(this::toTodoResponse).collect(Collectors.toList());
    }

    public TodoResponse createTodo(TodoRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
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

    @Transactional
    public TodoResponse toggleTodoCompletion(TodoToggleRequest request) {
        Todo todo = getTodoOrThrow(request.getTodoId());
        todo.setCompleted(!todo.isCompleted());
        return toTodoResponse(todo);
    }


    public List<ExamScheduleResponse> getAllExamSchedules(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);

        return examScheduleRepository.findAllByUserOrderByExamDateAsc(user)
                .stream()
                .map(e -> ExamScheduleResponse.builder()
                        .id(e.getId())
                        .examName(e.getExamName())  // ✅ 필드명에 맞게 변경
                        .examDate(e.getExamDate())
                        .build()
                )
                .collect(Collectors.toList());
    }


    public void createExamSchedule(ExamScheduleRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);

        examScheduleRepository.save(
                ExamSchedule.builder()
                        .user(user)
                        .examName(request.getExamName())  // ✅ examName → title 필드에 매핑
                        .examDate(request.getExamDate())
                        .build()
        );
    }


    public DDayInfoResponse getNearestExamSchedule(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        return examScheduleRepository.findNearestExamSchedule(user, LocalDate.now())
                .map(e -> {
                    long diff = LocalDate.now().until(e.getExamDate()).getDays();
                    String dday = diff == 0 ? "D-day" : (diff > 0 ? "D-" + diff : "D+" + Math.abs(diff));
                    return DDayInfoResponse.builder().title(e.getExamName()).examDate(e.getExamDate()).ddayText(dday).build();
                }).orElse(null);
    }

    public TotalStatsResponse getTotalStudyStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, LocalDate.of(2000, 1, 1), LocalDate.now());
        return TotalStatsResponse.builder()
                .totalStudyMinutes(sessions.stream().mapToInt(StudySession::getDurationMinutes).sum())
                .averageFocusRate((int) sessions.stream().mapToInt(StudySession::getFocusRate).average().orElse(0))
                .averageAccuracy((int) sessions.stream().mapToInt(StudySession::getAccuracy).average().orElse(0))
                .build();
    }

    public WeeklyStatsResponse getWeeklyStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        // 📌 1. 초기 7일치 맵 생성
        Map<LocalDate, WeeklyStatsResponse.DailyStat> map = new TreeMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            map.put(date, WeeklyStatsResponse.DailyStat.builder()
                    .date(date)
                    .studyMinutes(0)
                    .warningCount(0)
                    .build());
        }

        // 📌 2. 세션 데이터 조회 및 누적
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, startDate, today);
        for (StudySession session : sessions) {
            LocalDate date = session.getStudyDate();
            WeeklyStatsResponse.DailyStat stat = map.get(date);
            if (stat != null) {
                stat.setStudyMinutes(stat.getStudyMinutes() + session.getDurationMinutes());
                stat.setWarningCount(stat.getWarningCount() + session.getWarningCount()); // warningCount 필드 전제
            }
        }

        // 📌 3. 임시 growthList (향후 DB 연동 가능)
        List<GrowthResponse> growthList = List.of(
                new GrowthResponse("수학", 12),
                new GrowthResponse("영어", 8),
                new GrowthResponse("국어", 15),
                new GrowthResponse("과학", 5)
        );

        // 📌 4. 최종 DTO 구성
        return WeeklyStatsResponse.builder()
                .dailyStats(new ArrayList<>(map.values()))
                .growthList(growthList)
                .build();
    }


    public MonthlyStatsResponse getMonthlyStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, end);
        int total = sessions.stream().mapToInt(StudySession::getDurationMinutes).sum();

        Map<Integer, List<StudySession>> byWeek = groupByWeekOfMonth(sessions);

        List<Integer> weeklyAvgFocus = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<StudySession> weekSessions = byWeek.getOrDefault(i, List.of());
            int avg = (int) weekSessions.stream()
                    .mapToInt(StudySession::getFocusRate)
                    .average().orElse(0);
            weeklyAvgFocus.add(avg);
        }

        return MonthlyStatsResponse.builder()
                .totalStudyMinutes(total)
                .weeklyAverageFocusRates(weeklyAvgFocus)
                .build();
    }

    private Map<LocalDate, List<StudySession>> getStudySessionsByDate(User user, LocalDate start, LocalDate end) {
        return studySessionRepository.findByUserAndStudyDateBetween(user, start, end).stream()
                .collect(Collectors.groupingBy(StudySession::getStudyDate));
    }

    private Map<Integer, List<StudySession>> groupByWeekOfMonth(List<StudySession> sessions) {
        return sessions.stream().collect(Collectors.groupingBy(
                s -> (s.getStudyDate().getDayOfMonth() - 1) / 7 // 0~4 → 주차
        ));
    }


    public BestFocusDayResponse getBestFocusDay(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate start = LocalDate.now().minusDays(30);
        return studySessionRepository.findTopFocusDay(user, start).stream().findFirst()
                .map(s -> BestFocusDayResponse.builder().bestDay(s.getStudyDate()).bestFocusRate(s.getFocusRate()).build())
                .orElse(null);
    }

    public GoalSuggestionResponse getSuggestedGoal(HttpServletRequest httpRequest) {
        getSessionUser(httpRequest); // 유효성만 확인
        return GoalSuggestionResponse.builder().message("최근 집중률을 고려해 하루 2.5시간을 추천합니다.").suggestedDailyGoalMinutes(150).build();
    }

    public void updateGoalManually(GoalUpdateRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        goalRepository.save(Goal.builder()
                .user(user)
                .dailyGoalMinutes(request.getDailyGoalMinutes())
                .isSuggested(false)
                .setAt(LocalDate.now().atStartOfDay())
                .build());
    }

    public List<SubjectStatsResponse> getSubjectStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, LocalDate.of(2000, 1, 1), LocalDate.now());
        Map<String, List<StudySession>> bySubject = sessions.stream().filter(s -> s.getSubject() != null)
                .collect(Collectors.groupingBy(StudySession::getSubject));

        return bySubject.entrySet().stream().map(entry -> {
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
        }).collect(Collectors.toList());
    }

    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return getUser(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }

    private Todo getTodoOrThrow(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
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

    @Transactional
    public void updateStudyTime(StudyTimeUpdateRequest request, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        StudyTime studyTime = studyTimeRepository.findByUser(user)
                .orElse(StudyTime.builder().user(user).build());

        studyTime.setWeeklyGoalMinutes(request.getWeeklyGoalMinutes());
        studyTime.setTodayGoalMinutes(request.getTodayGoalMinutes());
        studyTime.setTodayStudyMinutes(request.getTodayStudyMinutes());

        studyTimeRepository.save(studyTime);
    }

    public List<NoticeResponse> getNotices() {
        return noticeRepository.findAll().stream()
                .map(n -> new NoticeResponse(n.getType(), n.getText(), n.getDate()))
                .toList();
    }

}
