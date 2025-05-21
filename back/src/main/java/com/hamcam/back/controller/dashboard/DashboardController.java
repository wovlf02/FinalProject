package com.hamcam.back.controller.dashboard;

import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.reflection.request.OptionReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.RangeReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.WeeklyReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.response.WeeklyReflectionResponse;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.todo.request.TodoRequest;
import com.hamcam.back.dto.dashboard.todo.request.TodoUpdateRequest;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.service.dashboard.DashboardService;
import com.hamcam.back.service.dashboard.GPTReflectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final GPTReflectionService gptReflectionService;

    // 📆 1. 월별 캘린더 이벤트
    @GetMapping("/calendar")
    public List<CalendarEventDto> getMonthlyCalendarEvents(
            @RequestParam("userId") Long userId,
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        return dashboardService.getMonthlyCalendarEvents(userId, month);
    }

    // 📆 2. 특정 날짜 Todo
    @GetMapping("/todos")
    public List<TodoResponse> getTodosByDate(
            @RequestParam("userId") Long userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return dashboardService.getTodosByDate(userId, date);
    }

    // 📆 3. Todo 생성
    @PostMapping("/todos")
    public void createTodo(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody TodoRequest request
    ) {
        dashboardService.createTodo(userId, request);
    }

    // 📆 4. Todo 수정
    @PutMapping("/todos/{todoId}")
    public void updateTodo(
            @RequestParam("userId") Long userId,
            @PathVariable Long todoId,
            @Valid @RequestBody TodoUpdateRequest request
    ) {
        dashboardService.updateTodo(userId, todoId, request);
    }

    // 📆 5. Todo 삭제
    @DeleteMapping("/todos/{todoId}")
    public void deleteTodo(
            @RequestParam("userId") Long userId,
            @PathVariable Long todoId
    ) {
        dashboardService.deleteTodo(userId, todoId);
    }

    // 📆 6. Todo 완료 체크 (토글)
    @PutMapping("/todos/{todoId}/complete")
    public TodoResponse toggleTodo(
            @RequestParam("userId") Long userId,
            @PathVariable Long todoId
    ) {
        return dashboardService.toggleTodoCompletion(userId, todoId);
    }

    // 📅 7. 전체 시험 일정 조회
    @GetMapping("/exams")
    public List<ExamScheduleResponse> getExamSchedules(@RequestParam("userId") Long userId) {
        return dashboardService.getAllExamSchedules(userId);
    }

    // 📅 8. 시험 일정 등록
    @PostMapping("/exams")
    public void createExamSchedule(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody ExamScheduleRequest request
    ) {
        dashboardService.createExamSchedule(userId, request);
    }

    // 📅 9. D-Day
    @GetMapping("/exams/nearest")
    public DDayInfoResponse getNearestExam(@RequestParam("userId") Long userId) {
        return dashboardService.getNearestExamSchedule(userId);
    }

    // 📊 10. 전체 학습 통계
    @GetMapping("/stats/total")
    public TotalStatsResponse getTotalStats(@RequestParam("userId") Long userId) {
        return dashboardService.getTotalStudyStats(userId);
    }

    // 📊 11. 과목별 통계
    @GetMapping("/stats/subjects")
    public List<SubjectStatsResponse> getSubjectStats(@RequestParam("userId") Long userId) {
        return dashboardService.getSubjectStats(userId);
    }

    // 📊 12. 주간 집중
    @GetMapping("/stats/weekly")
    public WeeklyStatsResponse getWeeklyStats(@RequestParam("userId") Long userId) {
        return dashboardService.getWeeklyStats(userId);
    }

    // 📊 13. 월간 집중
    @GetMapping("/stats/monthly")
    public MonthlyStatsResponse getMonthlyStats(@RequestParam("userId") Long userId) {
        return dashboardService.getMonthlyStats(userId);
    }

    // 📊 14. 최고 집중일
    @GetMapping("/stats/best-day")
    public BestFocusDayResponse getBestFocusDay(@RequestParam("userId") Long userId) {
        return dashboardService.getBestFocusDay(userId);
    }

    // 🏁 15. GPT 기반 목표 제안
    @GetMapping("/goal/suggest")
    public GoalSuggestionResponse suggestGoal(@RequestParam("userId") Long userId) {
        return dashboardService.getSuggestedGoal(userId);
    }

    // 🏁 16. 수동 목표 수정
    @PutMapping("/goal")
    public void updateGoal(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody GoalUpdateRequest request
    ) {
        dashboardService.updateGoalManually(userId, request);
    }

    // 🧠 17. GPT 주간 회고
    @PostMapping("/reflection/weekly")
    public WeeklyReflectionResponse generateWeeklyReflection(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody WeeklyReflectionRequest request
    ) {
        return gptReflectionService.generateWeeklyReflection(userId, request);
    }

    // 🧠 18. 기간 회고
    @PostMapping("/reflection/range")
    public WeeklyReflectionResponse generateReflectionByRange(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody RangeReflectionRequest request
    ) {
        return gptReflectionService.generateReflectionByRange(userId, request);
    }

    // 🧠 19. 옵션 회고
    @PostMapping("/reflection/custom")
    public WeeklyReflectionResponse generateCustomReflection(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody OptionReflectionRequest request
    ) {
        return gptReflectionService.generateCustomReflection(userId, request);
    }
}
