package com.hamcam.back.service.dashboard;

import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.calendar.request.CalendarRequest;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.community.notice.response.NoticeResponse;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.time.request.StudyTimeUpdateRequest;
import com.hamcam.back.dto.dashboard.todo.request.*;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.notice.NoticeRepository;
import com.hamcam.back.repository.dashboard.*;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hamcam.back.service.auth.SessionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private final SessionService sessionService;

    public List<CalendarEventDto> getMonthlyCalendarEvents(CalendarRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate start = request.getMonth().atDay(1);
        LocalDate end = request.getMonth().atEndOfMonth();

        List<Todo> todos = todoRepository.findAllByUserAndTodoDateBetween(user, start, end);
        List<ExamSchedule> exams = examScheduleRepository.findByUserId(user.getId())
                .stream().filter(e -> !e.getExamDate().isBefore(start) && !e.getExamDate().isAfter(end)).toList();
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, end);

        Map<LocalDate, CalendarEventDto> map = new HashMap<>();
        todos.forEach(todo -> map.computeIfAbsent(todo.getTodoDate(), d -> new CalendarEventDto(d)).getTodos().add(todo.getTitle()));
        exams.forEach(exam -> map.computeIfAbsent(exam.getExamDate(), d -> new CalendarEventDto(d)).getExams().add(exam.getTitle()));
        sessions.forEach(s -> map.computeIfAbsent(s.getStudyDate(), d -> new CalendarEventDto(d)).setTotalStudyMinutes(
                map.getOrDefault(s.getStudyDate(), new CalendarEventDto(s.getStudyDate())).getTotalStudyMinutes() + s.getDurationMinutes()));

        return map.values().stream().sorted(Comparator.comparing(CalendarEventDto::getDate)).collect(Collectors.toList());
    }

    public List<TodoResponse> getTodosByDate(TodoDateRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        return todoRepository.findAllByUserAndTodoDateOrderByPriorityDesc(user, request.getDate())
                .stream().map(this::toTodoResponse).collect(Collectors.toList());
    }

    @Transactional
    public TodoResponse createTodo(TodoRequest request, User user) {
        log.info("📝 Todo 생성 요청 - title: {}, date: {}, priority: {}", 
            request.getTitle(), request.getTodoDate(), request.getPriority());
            
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .todoDate(request.getTodoDate())
                .priority(request.getPriority())
                .completed(false)
                .user(user)
                .build();
                
        log.info("📝 Todo 생성 - id: {}, date: {}", todo.getId(), todo.getTodoDate());
        
        return toTodoResponse(todoRepository.save(todo));
    }

    public void updateTodo(TodoUpdateRequest request) {
        Todo todo = getTodoOrThrow(request.getTodoId());
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setTodoDate(request.getTodoDate());
        todo.setPriority(convertPriority(request.getPriority()));
    }

    @Transactional
    public void deleteTodo(TodoDeleteRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        Todo todo = getTodoOrThrow(request.getTodoId());
        
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        
        todoRepository.delete(todo);
    }

    @Transactional
    public void toggleTodoCompletion(TodoToggleRequest request) {
        if (request == null || request.getTodoId() == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Todo todo = getTodoOrThrow(request.getTodoId());
        todo.setCompleted(!todo.isCompleted());
        
        if (todo.isCompleted()) {
            todoRepository.delete(todo);
        }
    }

    @Transactional(readOnly = true)
    public List<ExamScheduleResponse> getAllExamSchedules(HttpServletRequest httpRequest) {
        User user = sessionService.getCurrentUser(httpRequest);
        List<ExamSchedule> schedules = examScheduleRepository.findAllByUserOrderByExamDateAsc(user);
        return schedules.stream()
                .map(schedule -> ExamScheduleResponse.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .subject(schedule.getSubject())
                        .examDate(schedule.getExamDate())
                        .description(schedule.getDescription())
                        .location(schedule.getLocation())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createExamSchedule(ExamScheduleRequest request, HttpServletRequest httpRequest) {
        log.info("📝 시험 일정 등록 요청 - title: {}, subject: {}, date: {}", 
            request.getTitle(), request.getSubject(), request.getExamDate());
            
        User user = sessionService.getCurrentUser(httpRequest);
        
        // 시험 날짜가 현재보다 이전인지 확인
        if (request.getExamDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 사용자의 현재 등록된 시험 일정 개수 확인
        List<ExamSchedule> existingSchedules = examScheduleRepository.findAllByUserOrderByExamDateAsc(user);
        if (existingSchedules.size() >= 3) {
            throw new CustomException(ErrorCode.EXAM_SCHEDULE_LIMIT_EXCEEDED);
        }
        
        ExamSchedule schedule = ExamSchedule.builder()
                .user(user)
                .title(request.getTitle())
                .subject(request.getSubject())
                .examDate(request.getExamDate())
                .description(request.getDescription())
                .location(request.getLocation())
                .build();
                
        examScheduleRepository.save(schedule);
        log.info("✅ 시험 일정 등록 완료 - id: {}, title: {}", schedule.getId(), schedule.getTitle());
    }

    @Transactional(readOnly = true)
    public DDayInfoResponse getNearestExamSchedule(HttpServletRequest httpRequest) {
        User user = sessionService.getCurrentUser(httpRequest);
        LocalDate now = LocalDate.now();
        ExamSchedule nearestExam = examScheduleRepository.findFirstByUserAndExamDateAfterOrderByExamDateAsc(user, now);
        
        if (nearestExam == null) {
            return DDayInfoResponse.builder()
                    .title("다가오는 시험이 없습니다")
                    .dDay(0L)
                    .build();
        }

        long dDay = ChronoUnit.DAYS.between(now, nearestExam.getExamDate());
        return DDayInfoResponse.builder()
                .title(nearestExam.getTitle())
                .dDay(Long.valueOf(dDay))
                .build();
    }

    public TotalStatsResponse getTotalStudyStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(
                user, LocalDate.of(2000, 1, 1), LocalDate.now()
        );

        int totalMinutes = sessions.stream()
                .mapToInt(StudySession::getDurationMinutes)
                .sum();

        int avgFocusRate = (int) Math.round(
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

        return TotalStatsResponse.builder()
                .totalStudyMinutes(totalMinutes)
                .averageFocusRate(avgFocusRate)
                .averageAccuracy(avgAccuracy)
                .build();
    }

    public WeeklyStatsResponse getWeeklyStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        Map<LocalDate, WeeklyStatsResponse.DailyStat> map = new TreeMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            map.put(date, WeeklyStatsResponse.DailyStat.builder()
                    .date(date)
                    .studyMinutes(0)
                    .warningCount(0)
                    .build());
        }

        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, startDate, today);
        for (StudySession session : sessions) {
            LocalDate date = session.getStudyDate();
            WeeklyStatsResponse.DailyStat stat = map.get(date);
            if (stat != null) {
                stat.setStudyMinutes(stat.getStudyMinutes() + session.getDurationMinutes());
                stat.setWarningCount(stat.getWarningCount() + session.getWarningCount());
            }
        }

        List<GrowthResponse> growthList = List.of(
                new GrowthResponse("수학", 12),
                new GrowthResponse("영어", 8),
                new GrowthResponse("국어", 15),
                new GrowthResponse("과학", 5)
        );

        return WeeklyStatsResponse.builder()
                .dailyStats(new ArrayList<>(map.values()))
                .growthList(growthList)
                .build();
    }

    public MonthlyStatsResponse getMonthlyStats(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(user, start, end);
        int total = sessions.stream().mapToInt(StudySession::getDurationMinutes).sum();

        Map<Integer, List<StudySession>> byWeek = groupByWeekOfMonth(sessions);

        List<Integer> weeklyAvgFocus = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<StudySession> weekSessions = byWeek.getOrDefault(i, List.of());

            int avg = (int) Math.round(
                    weekSessions.stream()
                            .mapToDouble(StudySession::getFocusRate)
                            .average()
                            .orElse(0.0)
            );

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
                s -> (s.getStudyDate().getDayOfMonth() - 1) / 7
        ));
    }

    public BestFocusDayResponse getBestFocusDay(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        LocalDate start = LocalDate.now().minusDays(30);

        return studySessionRepository.findTopFocusDay(user, start).stream()
                .findFirst()
                .map(s -> BestFocusDayResponse.builder()
                        .bestDay(s.getStudyDate())
                        .bestFocusRate((int) Math.round(s.getFocusRate()))
                        .build())
                .orElse(null);
    }

    public GoalSuggestionResponse getSuggestedGoal(HttpServletRequest httpRequest) {
        getSessionUser(httpRequest);
        return GoalSuggestionResponse.builder()
                .message("최근 집중률을 고려해 하루 2.5시간을 추천합니다.")
                .suggestedDailyGoalMinutes(150)
                .build();
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
        List<StudySession> sessions = studySessionRepository.findByUserAndStudyDateBetween(
                user, LocalDate.of(2000, 1, 1), LocalDate.now()
        );

        Map<String, List<StudySession>> bySubject = sessions.stream()
                .filter(s -> s.getSubject() != null)
                .collect(Collectors.groupingBy(StudySession::getSubject));

        return bySubject.entrySet().stream().map(entry -> {
            String subject = entry.getKey();
            List<StudySession> subjectSessions = entry.getValue();

            int totalFocus = subjectSessions.stream()
                    .mapToInt(StudySession::getDurationMinutes)
                    .sum();

            int avgAccuracy = (int) Math.round(subjectSessions.stream()
                    .mapToDouble(StudySession::getAccuracy)
                    .average()
                    .orElse(0.0));

            int avgCorrectRate = (int) Math.round(subjectSessions.stream()
                    .mapToDouble(StudySession::getCorrectRate)
                    .average()
                    .orElse(0.0));

            return SubjectStatsResponse.builder()
                    .subjectName(subject)
                    .totalFocusMinutes(totalFocus)
                    .averageAccuracy(avgAccuracy)
                    .averageCorrectRate(avgCorrectRate)
                    .build();
        }).collect(Collectors.toList());
    }

    public User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return getUser(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }

    private Todo getTodoOrThrow(Long todoId) {
        if (todoId == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
    }

    private TodoResponse toTodoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .todoDate(todo.getTodoDate())
                .priority(todo.getPriority())
                .completed(todo.isCompleted())
                .build();
    }

    private StudyTime createDefaultStudyTime(User user) {
        return StudyTime.createDefault(user);
    }

    @Transactional
    public StudyTime updateStudyTime(StudyTimeUpdateRequest request) {
        try {
            User user = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
            
            StudyTime studyTime = studyTimeRepository.findByUser(user)
                .orElseGet(() -> createDefaultStudyTime(user));

            studyTime.updateGoals(request.getWeeklyGoalMinutes(), request.getTodayGoalMinutes());
            return studyTimeRepository.save(studyTime);
        } catch (Exception e) {
            throw new RuntimeException("공부 시간 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public List<NoticeResponse> getNotices() {
        return noticeRepository.findAll().stream()
                .map(n -> NoticeResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .views(n.getViews())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos(HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        List<Todo> todos = todoRepository.findAllByUserOrderByTodoDateDesc(user);
        log.info("📋 Todo 목록 조회 - user: {}, count: {}", user.getId(), todos.size());
        todos.forEach(todo -> log.info("  - id: {}, date: {}, title: {}", 
            todo.getId(), todo.getTodoDate(), todo.getTitle()));
        return todos.stream().map(this::toTodoResponse).collect(Collectors.toList());
    }

    private PriorityLevel convertPriority(PriorityLevel priorityLevel) {
        return priorityLevel;
    }

    public void deleteExamSchedule(Long examId, HttpServletRequest httpRequest) {
        Long userId = SessionUtil.getUserId(httpRequest);
        ExamSchedule exam = examScheduleRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("시험 일정을 찾을 수 없습니다."));
        
        if (!exam.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 시험 일정만 삭제할 수 있습니다.");
        }
        
        examScheduleRepository.delete(exam);
    }
}
