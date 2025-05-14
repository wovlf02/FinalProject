package com.hamcam.back.controller.exam;

import com.hamcam.back.entity.exam.Problem;
import com.hamcam.back.service.exam.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "http://localhost:3000")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @GetMapping("/questions")
    public ResponseEntity<List<Problem>> getQuestionsBySubject(
        @RequestParam String subject,
        @RequestParam(required = false) Integer count,
        @RequestParam(required = false) String difficulty
    ) {
        List<Problem> all = problemService.getQuestionsBySubject(subject);

        // 난이도 필터링
        if (difficulty != null && !difficulty.isEmpty()) {
            double min = 0, max = 100;
            switch (difficulty) {
                case "high": max = 29; break; // 상: 0~29%
                case "medium": min = 30; max = 79; break; // 중: 50~79%
                case "low": min = 80; max = 100; break; // 하: 80~100%
            }
            final double minFinal = min;
            final double maxFinal = max;
            all = all.stream()
                .filter(p -> p.getCorrectRate() != null && p.getCorrectRate() >= minFinal && p.getCorrectRate() <= maxFinal)
                .toList();
        }

        if (count != null && count > 0 && all.size() > count) {
            all = new ArrayList<>(all);
            Collections.shuffle(all);
            all = all.subList(0, count);
        }
        return ResponseEntity.ok(all);
    }

    @PostMapping("/questions")
    public ResponseEntity<Problem> saveQuestion(@RequestBody Problem problem) {
        return ResponseEntity.ok(problemService.saveQuestion(problem));
    }

    // 문제 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        problemService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }
} 