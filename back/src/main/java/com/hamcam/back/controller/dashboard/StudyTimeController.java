package com.hamcam.back.controller.dashboard;

import com.hamcam.back.dto.dashboard.time.request.StudyTimeUpdateRequest;
import com.hamcam.back.service.dashboard.StudyTimeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StudyTimeController {

    private final StudyTimeService studyTimeService;

    @GetMapping("/study-time/get")
    public ResponseEntity<?> getStudyTime(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(studyTimeService.getStudyTime(request));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("공부 시간 조회 중 오류가 발생했습니다", e.getMessage()));
        }
    }

    @PostMapping("/study-time/update")
    public ResponseEntity<?> updateStudyTime(@RequestBody StudyTimeUpdateRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(studyTimeService.updateStudyTime(request, httpRequest));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("공부 시간 업데이트 중 오류가 발생했습니다", e.getMessage()));
        }
    }

    private record ErrorResponse(String message, String detail) {}
} 