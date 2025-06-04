package com.hamcam.back.controller.exam;

import com.hamcam.back.entity.exam.ExamResult;
import com.hamcam.back.entity.ExamResultDetail;
import com.hamcam.back.repository.exam.ExamResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ExamResultController {

    private final ExamResultRepository examResultRepository;

    @PostMapping
    public ResponseEntity<ExamResult> saveResult(@RequestBody ExamResult result) {
        // details의 각 자식에 부모 연결
        if (result.getDetails() != null) {
            for (ExamResultDetail detail : result.getDetails()) {
                detail.setExamResult(result);
            }
        }
        try {
            return ResponseEntity.ok(examResultRepository.save(result));
        } catch (DataIntegrityViolationException e) {
            // 중복된 결과가 있는 경우 기존 결과 반환
            List<ExamResult> existingResults = examResultRepository.findByUserIdOrderByDateDesc(result.getUserId());
            for (ExamResult existing : existingResults) {
                if (existing.getSubject().equals(result.getSubject()) && 
                    existing.getDate().equals(result.getDate())) {
                    return ResponseEntity.ok(existing);
                }
            }
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<ExamResult>> getResults(@RequestParam String userId) {
        return ResponseEntity.ok(examResultRepository.findByUserIdOrderByDateDesc(userId));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteAllResults(@RequestParam String userId) {
        examResultRepository.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResult> getResultById(@PathVariable Long id) {
        return ResponseEntity.ok(
            examResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결과를 찾을 수 없습니다."))
        );
    }
} 