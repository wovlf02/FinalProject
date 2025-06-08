package com.hamcam.back.controller.dashboard;

import com.hamcam.back.dto.common.MessageResponse;
import com.hamcam.back.dto.community.notice.response.NoticeResponse;
import com.hamcam.back.dto.dashboard.calendar.CalendarEventDto;
import com.hamcam.back.dto.dashboard.calendar.request.CalendarRequest;
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
import com.hamcam.back.dto.dashboard.time.request.StudyTimeUpdateRequest;
import com.hamcam.back.dto.dashboard.todo.request.*;
import com.hamcam.back.dto.dashboard.todo.response.TodoResponse;
import com.hamcam.back.service.dashboard.DashboardService;
import com.hamcam.back.service.dashboard.GPTReflectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final GPTReflectionService gptReflectionService;
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    // 📆 월별 캘린더 이벤트
    @PostMapping("/calendar")
    public ResponseEntity<List<CalendarEventDto>> getMonthlyCalendarEvents(
            @RequestBody CalendarRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(dashboardService.getMonthlyCalendarEvents(request, httpRequest));
    }

    // 📅 특정 날짜 Todo
    @PostMapping("/todos/date")
    public ResponseEntity<List<TodoResponse>> getTodosByDate(
            @RequestBody TodoDateRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(dashboardService.getTodosByDate(request, httpRequest));
    }

    // ✅ Todo 생성
    @PostMapping("/todos")
    public ResponseEntity<MessageResponse> createTodo(@RequestBody TodoRequest request, HttpServletRequest httpRequest) {
        log.info("📝 Todo 생성 요청 - title: {}, date: {}, priority: {}", 
            request.getTitle(), request.getTodoDate(), request.getPriority());
        dashboardService.createTodo(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("✅ Todo가 생성되었습니다."));
    }

    // ✅ Todo 수정
    @PutMapping("/todos")
    public ResponseEntity<MessageResponse> updateTodo(@RequestBody TodoUpdateRequest request) {
        dashboardService.updateTodo(request);
        return ResponseEntity.ok(MessageResponse.of("✏️ Todo가 수정되었습니다."));
    }

    // ✅ Todo 삭제
    @PostMapping("/todos/delete")
    public ResponseEntity<MessageResponse> deleteTodo(
            @RequestBody TodoDeleteRequest request,
            HttpServletRequest httpRequest
    ) {
        dashboardService.deleteTodo(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("🗑️ Todo가 삭제되었습니다."));
    }

    // ✅ Todo 완료 토글
    @PutMapping("/todos/complete")
    public ResponseEntity<MessageResponse> toggleTodoCompletion(
            @RequestBody TodoToggleRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("�� Todo 완료 상태 변경 요청 - request: {}", request);
        log.info("🔄 Todo 완료 상태 변경 요청 - todoId: {}", request.getTodoId());
        try {
            dashboardService.toggleTodoCompletion(request);
            return ResponseEntity.ok(MessageResponse.of("✅ Todo가 완료되었습니다."));
        } catch (Exception e) {
            log.error("❌ Todo 완료 상태 변경 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 🗓 시험 일정 전체 조회
    @PostMapping("/exams")
    public ResponseEntity<List<ExamScheduleResponse>> getExamSchedules(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getAllExamSchedules(httpRequest));
    }

    // 🗓 시험 일정 등록
    @PostMapping("/exams/register")
    public ResponseEntity<MessageResponse> createExamSchedule(
            @RequestBody ExamScheduleRequest request,
            HttpServletRequest httpRequest
    ) {
        dashboardService.createExamSchedule(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("✅ 시험 일정이 등록되었습니다."));
    }

    // 🗓 D-Day 조회
    @PostMapping("/exams/nearest")
    public ResponseEntity<DDayInfoResponse> getNearestExam(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getNearestExamSchedule(httpRequest));
    }

    // 📊 전체 통계
    @PostMapping("/stats/total")
    public ResponseEntity<TotalStatsResponse> getTotalStats(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getTotalStudyStats(httpRequest));
    }

    // 📊 과목별 통계
    @PostMapping("/stats/subjects")
    public ResponseEntity<List<SubjectStatsResponse>> getSubjectStats(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getSubjectStats(httpRequest));
    }

    // 📊 주간 통계
    @PostMapping("/stats/weekly")
    public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getWeeklyStats(httpRequest));
    }

    // 📊 월간 통계
    @PostMapping("/stats/monthly")
    public ResponseEntity<MonthlyStatsResponse> getMonthlyStats(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getMonthlyStats(httpRequest));
    }

    // 📊 최고 집중일
    @PostMapping("/stats/best-day")
    public ResponseEntity<BestFocusDayResponse> getBestFocusDay(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getBestFocusDay(httpRequest));
    }

    // 🏁 목표 제안
    @PostMapping("/goal/suggest")
    public ResponseEntity<GoalSuggestionResponse> suggestGoal(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getSuggestedGoal(httpRequest));
    }

    // 🏁 목표 수동 설정
    @PutMapping("/goal")
    public ResponseEntity<MessageResponse> updateGoal(
            @RequestBody GoalUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        dashboardService.updateGoalManually(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("🎯 목표가 업데이트되었습니다."));
    }

    // 🧠 주간 회고
    @PostMapping("/reflection/weekly")
    public ResponseEntity<WeeklyReflectionResponse> generateWeeklyReflection(
            @RequestBody WeeklyReflectionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(gptReflectionService.generateWeeklyReflection(request, httpRequest));
    }

    // 🧠 기간 회고
    @PostMapping("/reflection/range")
    public ResponseEntity<WeeklyReflectionResponse> generateReflectionByRange(
            @RequestBody RangeReflectionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(gptReflectionService.generateReflectionByRange(request, httpRequest));
    }

    // 🧠 선택 기반 회고
    @PostMapping("/reflection/custom")
    public ResponseEntity<WeeklyReflectionResponse> generateCustomReflection(
            @RequestBody OptionReflectionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(gptReflectionService.generateCustomReflection(request, httpRequest));
    }

    @PostMapping("/study-time")
    public ResponseEntity<MessageResponse> updateStudyTime(
            @RequestBody StudyTimeUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        dashboardService.updateStudyTime(request, httpRequest);
        return ResponseEntity.ok(MessageResponse.of("공부시간 설정이 저장되었습니다."));
    }

    // 📢 공지사항 조회
    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponse>> getNotices() {
        return ResponseEntity.ok(dashboardService.getNotices());
    }

    // 📅 모든 Todo 조회
    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponse>> getAllTodos(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(dashboardService.getAllTodos(httpRequest));
    }

}
