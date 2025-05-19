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

    // 📆 1. 월별 캘린더 이벤트 (Todo + 시험 + 공부 기록)
    @GetMapping("/calendar")
    public List<CalendarEventDto> getMonthlyCalendarEvents(
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return dashboardService.getMonthlyCalendarEvents(month);
    }

    // 📆 2. 특정 날짜의 Todo 목록
    @GetMapping("/todos")
    public List<TodoResponse> getTodosByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dashboardService.getTodosByDate(date);
    }

    // 📆 3. Todo 생성
    @PostMapping("/todos")
    public void createTodo(@Valid @RequestBody TodoRequest request) {
        dashboardService.createTodo(request);
    }

    // 📆 4. Todo 수정
    @PutMapping("/todos/{todoId}")
    public void updateTodo(@PathVariable Long todoId, @Valid @RequestBody TodoUpdateRequest request) {
        dashboardService.updateTodo(todoId, request);
    }

    // 📆 5. Todo 삭제
    @DeleteMapping("/todos/{todoId}")
    public void deleteTodo(@PathVariable Long todoId) {
        dashboardService.deleteTodo(todoId);
    }

    // 📆 6. Todo 완료 체크
    @PutMapping("/todos/{todoId}/complete")
    public void completeTodo(@PathVariable Long todoId) {
        dashboardService.completeTodo(todoId);
    }

    // 📅 7. 전체 시험 일정 조회
    @GetMapping("/exams")
    public List<ExamScheduleResponse> getExamSchedules() {
        return dashboardService.getAllExamSchedules();
    }

    // 📅 8. 시험 일정 등록
    @PostMapping("/exams")
    public void createExamSchedule(@Valid @RequestBody ExamScheduleRequest request) {
        dashboardService.createExamSchedule(request);
    }

    // 📅 9. 가장 가까운 시험 일정 D-Day
    @GetMapping("/exams/nearest")
    public DDayInfoResponse getNearestExam() {
        return dashboardService.getNearestExamSchedule();
    }

    // 📊 10. 전체 학습 통계
    @GetMapping("/stats/total")
    public TotalStatsResponse getTotalStats() {
        return dashboardService.getTotalStudyStats();
    }

    // 📊 11. 과목별 학습 통계
    @GetMapping("/stats/subjects")
    public List<SubjectStatsResponse> getSubjectStats() {
        return dashboardService.getSubjectStats();
    }

    // 📊 12. 주간 집중 통계
    @GetMapping("/stats/weekly")
    public WeeklyStatsResponse getWeeklyStats() {
        return dashboardService.getWeeklyStats();
    }

    // 📊 13. 월간 집중 통계
    @GetMapping("/stats/monthly")
    public MonthlyStatsResponse getMonthlyStats() {
        return dashboardService.getMonthlyStats();
    }

    // 📊 14. 최근 30일 중 최고 집중일
    @GetMapping("/stats/best-day")
    public BestFocusDayResponse getBestFocusDay() {
        return dashboardService.getBestFocusDay();
    }

    // 🏁 15. GPT 기반 목표 제안
    @GetMapping("/goal/suggest")
    public GoalSuggestionResponse suggestGoal() {
        return dashboardService.getSuggestedGoal();
    }

    // 🏁 16. 목표 수동 수정
    @PutMapping("/goal")
    public void updateGoal(@Valid @RequestBody GoalUpdateRequest request) {
        dashboardService.updateGoalManually(request);
    }

    // 🧠 17. GPT 주간 회고 생성
    @PostMapping("/reflection/weekly")
    public WeeklyReflectionResponse generateWeeklyReflection(@RequestBody @Valid WeeklyReflectionRequest request) {
        return gptReflectionService.generateWeeklyReflection(request);
    }

    // 🧠 18. 기간 지정 회고
    @PostMapping("/reflection/range")
    public WeeklyReflectionResponse generateReflectionByRange(@RequestBody @Valid RangeReflectionRequest request) {
        return gptReflectionService.generateReflectionByRange(request);
    }

    // 🧠 19. 옵션 기반 회고 생성
    @PostMapping("/reflection/custom")
    public WeeklyReflectionResponse generateCustomReflection(@RequestBody @Valid OptionReflectionRequest request) {
        return gptReflectionService.generateCustomReflection(request);
    }
}
