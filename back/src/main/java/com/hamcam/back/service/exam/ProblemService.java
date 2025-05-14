package com.hamcam.back.service.exam;

import com.hamcam.back.entity.exam.Problem;
import com.hamcam.back.repository.exam.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;

    // 과목별 문제 조회
    public List<Problem> getQuestionsBySubject(String subject) {
        return problemRepository.findBySubject(subject);
    }

    // 문제 저장
    @Transactional
    public Problem saveQuestion(Problem problem) {
        return problemRepository.save(problem);
    }

    // 문제 삭제
    @Transactional
    public void deleteQuestion(Integer id) {
        problemRepository.deleteById(id);
    }
} 