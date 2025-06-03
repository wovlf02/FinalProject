package com.hamcam.back.controller.study.team;

import com.hamcam.back.dto.study.team.response.QuizProblemResponse;
import com.hamcam.back.service.study.team.QuizRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizRoomRestController {

    private final QuizRoomService quizRoomService;

    /**
     * ✅ 조건 기반 문제 랜덤 조회
     */
    @GetMapping("/problems/random")
    public QuizProblemResponse getRandomProblem(
            @RequestParam String subject,
            @RequestParam String source,
            @RequestParam String level
    ) {
        return quizRoomService.getRandomProblem(subject, source, level);
    }
}
