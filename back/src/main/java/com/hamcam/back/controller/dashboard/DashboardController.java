package com.hamcam.back.controller.dashboard;

import com.hamcam.back.dto.dashboard.calendar.request.CalendarRequest;
import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.exam.request.ExamScheduleRequest;
import com.hamcam.back.dto.dashboard.exam.request.ExamUserRequest;
import com.hamcam.back.dto.dashboard.exam.response.DDayInfoResponse;
import com.hamcam.back.dto.dashboard.exam.response.ExamScheduleResponse;
import com.hamcam.back.dto.dashboard.goal.request.GoalUpdateRequest;
import com.hamcam.back.dto.dashboard.goal.request.GoalUserRequest;
import com.hamcam.back.dto.dashboard.goal.response.GoalSuggestionResponse;
import com.hamcam.back.dto.dashboard.reflection.request.OptionReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.RangeReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.request.WeeklyReflectionRequest;
import com.hamcam.back.dto.dashboard.reflection.response.WeeklyReflectionResponse;
import com.hamcam.back.dto.dashboard.stats.request.StatsUserRequest;
import com.hamcam.back.dto.dashboard.stats.response.*;
import com.hamcam.back.dto.dashboard.todo.request.*;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.service.dashboard.DashboardService;
import com.hamcam.back.service.dashboard.GPTReflectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final GPTReflectionService gptReflectionService;

    /** 📆 월별 캘린더 이벤트 */
    @PostMapping("/calendar")
    public List<CalendarEventDto> getMonthlyCalendarEvents(@RequestBody @Valid CalendarRequest request) {
        return dashboardService.getMonthlyCalendarEvents(request);
    }

    /** 📅 특정 날짜의 Todo 조회 */
    @PostMapping("/todos/date")
    public List<TodoResponse> getTodosByDate(@RequestBody @Valid TodoDateRequest request) {
        return dashboardService.getTodosByDate(request);
    }

    /** ✅ Todo 생성 */
    @PostMapping("/todos")
    public void createTodo(@RequestBody @Valid TodoRequest request) {
        dashboardService.createTodo(request);
    }

    /** ✅ Todo 수정 */
    @PutMapping("/todos")
    public void updateTodo(@RequestBody @Valid TodoUpdateRequest request) {
        dashboardService.updateTodo(request);
    }

    /** ✅ Todo 삭제 */
    @PostMapping("/todos/delete")
    public void deleteTodo(@RequestBody @Valid TodoDeleteRequest request) {
        dashboardService.deleteTodo(request);
    }

    /** ✅ Todo 완료 상태 토글 */
    @PutMapping("/todos/complete")
    public TodoResponse toggleTodo(@RequestBody @Valid TodoToggleRequest request) {
        return dashboardService.toggleTodoCompletion(request);
    }

    /** 🗓 시험 일정 조회 */
    @PostMapping("/exams")
    public List<ExamScheduleResponse> getExamSchedules(@RequestBody @Valid ExamUserRequest request) {
        return dashboardService.getAllExamSchedules(request);
    }

    /** 🗓 시험 일정 등록 */
    @PostMapping("/exams/register")
    public void createExamSchedule(@RequestBody @Valid ExamScheduleRequest request) {
        dashboardService.createExamSchedule(request);
    }

    /** 🗓 D-Day 조회 */
    @PostMapping("/exams/nearest")
    public DDayInfoResponse getNearestExam(@RequestBody @Valid ExamUserRequest request) {
        return dashboardService.getNearestExamSchedule(request);
    }

    /** 📊 통계 */
    @PostMapping("/stats/total")
    public TotalStatsResponse getTotalStats(@RequestBody @Valid StatsUserRequest request) {
        return dashboardService.getTotalStudyStats(request);
    }

    @PostMapping("/stats/subjects")
    public List<SubjectStatsResponse> getSubjectStats(@RequestBody @Valid StatsUserRequest request) {
        return dashboardService.getSubjectStats(request);
    }

    @PostMapping("/stats/weekly")
    public WeeklyStatsResponse getWeeklyStats(@RequestBody @Valid StatsUserRequest request) {
        return dashboardService.getWeeklyStats(request);
    }

    @PostMapping("/stats/monthly")
    public MonthlyStatsResponse getMonthlyStats(@RequestBody @Valid StatsUserRequest request) {
        return dashboardService.getMonthlyStats(request);
    }

    @PostMapping("/stats/best-day")
    public BestFocusDayResponse getBestFocusDay(@RequestBody @Valid StatsUserRequest request) {
        return dashboardService.getBestFocusDay(request);
    }

    /** 🏁 목표 설정 */
    @PostMapping("/goal/suggest")
    public GoalSuggestionResponse suggestGoal(@RequestBody @Valid GoalUserRequest request) {
        return dashboardService.getSuggestedGoal(request);
    }

    @PutMapping("/goal")
    public void updateGoal(@RequestBody @Valid GoalUpdateRequest request) {
        dashboardService.updateGoalManually(request);
    }

    /** 🧠 회고 생성 (GPT 기반) */
    @PostMapping("/reflection/weekly")
    public WeeklyReflectionResponse generateWeeklyReflection(@RequestBody @Valid WeeklyReflectionRequest request) {
        return gptReflectionService.generateWeeklyReflection(request);
    }

    @PostMapping("/reflection/range")
    public WeeklyReflectionResponse generateReflectionByRange(@RequestBody @Valid RangeReflectionRequest request) {
        return gptReflectionService.generateReflectionByRange(request);
    }

    @PostMapping("/reflection/custom")
    public WeeklyReflectionResponse generateCustomReflection(@RequestBody @Valid OptionReflectionRequest request) {
        return gptReflectionService.generateCustomReflection(request);
    }
}
