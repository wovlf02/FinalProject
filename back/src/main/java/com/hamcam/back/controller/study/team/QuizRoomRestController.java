package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.rest.response.QuizProblemResponse;
import com.hamcam.back.service.study.team.rest.QuizRoomRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 퀴즈방에서 문제를 조건에 따라 랜덤으로 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/quiz/problems")
@RequiredArgsConstructor
public class QuizRoomRestController {

    private final QuizRoomRestService quizRoomService;

    /**
     * ✅ 조건(subject, source, level)에 따른 랜덤 문제 조회
     *
     * @param subject 과목명 (국어/수학/영어 등)
     * @param source 출처 (예: 2025년 수능)
     * @param level 난이도 (최하/하/중/상/최상)
     * @return 문제 + (국어인 경우 지문 포함)
     */
    @GetMapping("/random")
    public ResponseEntity<QuizProblemResponse> getRandomProblem(
            @RequestParam String subject,
            @RequestParam String source,
            @RequestParam String level
    ) {
        log.info("🔍 문제 요청 - subject={}, source={}, level={}", subject, source, level);
        QuizProblemResponse problem = quizRoomService.getRandomProblem(subject, source, level);
        return ResponseEntity.ok(problem);
    }
}
