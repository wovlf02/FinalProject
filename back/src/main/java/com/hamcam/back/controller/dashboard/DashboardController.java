package com.hamcam.back.controller.dashboard;

import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.dashboard.reflection.request.OptionReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.RangeReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.WeeklyReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.response.WeeklyReflectionResponse;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.todo.request.*;
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

    // 📆 월별 캘린더 이벤트
    @GetMapping("/calendar")
    public List<CalendarEventDto> getMonthlyCalendarEvents(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return dashboardService.getMonthlyCalendarEvents(userId, month);
    }

    // 📅 특정 날짜의 Todo 조회
    @GetMapping("/todos")
    public List<TodoResponse> getTodosByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dashboardService.getTodosByDate(userId, date);
    }

    // ✅ Todo 생성
    @PostMapping("/todos")
    public void createTodo(@Valid @RequestBody TodoRequest request) {
        dashboardService.createTodo(request.getUserId(), request);
    }

    // ✅ Todo 수정
    @PutMapping("/todos/{todoId}")
    public void updateTodo(
            @PathVariable Long todoId,
            @Valid @RequestBody TodoUpdateRequest request) {
        dashboardService.updateTodo(request.getUserId(), todoId, request);
    }

    // ✅ Todo 삭제
    @DeleteMapping("/todos")
    public void deleteTodo(@Valid @RequestBody TodoDeleteRequest request) {
        dashboardService.deleteTodo(request.getUserId(), request.getTodoId());
    }

    // ✅ Todo 완료 상태 토글
    @PutMapping("/todos/complete")
    public TodoResponse toggleTodo(@Valid @RequestBody TodoToggleRequest request) {
        return dashboardService.toggleTodoCompletion(request.getUserId(), request.getTodoId());
    }

    // 🗓 시험 일정 조회
    @GetMapping("/exams")
    public List<ExamScheduleResponse> getExamSchedules(@RequestParam Long userId) {
        return dashboardService.getAllExamSchedules(userId);
    }

    // 🗓 시험 일정 등록
    @PostMapping("/exams")
    public void createExamSchedule(@Valid @RequestBody ExamScheduleRequest request) {
        dashboardService.createExamSchedule(request.getUserId(), request);
    }

    // 🗓 D-Day 조회
    @GetMapping("/exams/nearest")
    public DDayInfoResponse getNearestExam(@RequestParam Long userId) {
        return dashboardService.getNearestExamSchedule(userId);
    }

    // 📊 통계
    @GetMapping("/stats/total")
    public TotalStatsResponse getTotalStats(@RequestParam Long userId) {
        return dashboardService.getTotalStudyStats(userId);
    }

    @GetMapping("/stats/subjects")
    public List<SubjectStatsResponse> getSubjectStats(@RequestParam Long userId) {
        return dashboardService.getSubjectStats(userId);
    }

    @GetMapping("/stats/weekly")
    public WeeklyStatsResponse getWeeklyStats(@RequestParam Long userId) {
        return dashboardService.getWeeklyStats(userId);
    }

    @GetMapping("/stats/monthly")
    public MonthlyStatsResponse getMonthlyStats(@RequestParam Long userId) {
        return dashboardService.getMonthlyStats(userId);
    }

    @GetMapping("/stats/best-day")
    public BestFocusDayResponse getBestFocusDay(@RequestParam Long userId) {
        return dashboardService.getBestFocusDay(userId);
    }

    // 🏁 목표 설정
    @GetMapping("/goal/suggest")
    public GoalSuggestionResponse suggestGoal(@RequestParam Long userId) {
        return dashboardService.getSuggestedGoal(userId);
    }

    @PutMapping("/goal")
    public void updateGoal(@Valid @RequestBody GoalUpdateRequest request) {
        dashboardService.updateGoalManually(request.getUserId(), request);
    }

    // 🧠 회고 생성 (GPT 기반)
    @PostMapping("/reflection/weekly")
    public WeeklyReflectionResponse generateWeeklyReflection(@Valid @RequestBody WeeklyReflectionRequest request) {
        return gptReflectionService.generateWeeklyReflection(request.getUserId(), request);
    }

    @PostMapping("/reflection/range")
    public WeeklyReflectionResponse generateReflectionByRange(@Valid @RequestBody RangeReflectionRequest request) {
        return gptReflectionService.generateReflectionByRange(request.getUserId(), request);
    }

    @PostMapping("/reflection/custom")
    public WeeklyReflectionResponse generateCustomReflection(@Valid @RequestBody OptionReflectionRequest request) {
        return gptReflectionService.generateCustomReflection(request.getUserId(), request);
    }
}
