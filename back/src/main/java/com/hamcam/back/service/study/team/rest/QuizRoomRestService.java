package com.hamcam.back.service.study.team;

import com.hamcam.back.entity.study.team.Problem;
import com.hamcam.back.entity.study.team.Passage;
import com.hamcam.back.dto.study.team.response.QuizProblemResponse;
import com.hamcam.back.repository.study.ProblemRepository;
import com.hamcam.back.repository.study.PassageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuizRoomRestService {

    private final ProblemRepository problemRepository;
    private final PassageRepository passageRepository;

    public QuizProblemResponse getRandomProblem(String subject, String source, String level) {
        // 난이도 기준 변환
        double[] range = mapLevelToCorrectRate(level); // [min, max]

        List<Problem> problems = problemRepository.findBySubjectAndSourceAndCorrectRateBetween(
                subject, source, range[0], range[1]
        );

        if (problems.isEmpty()) throw new IllegalArgumentException("조건에 맞는 문제가 없습니다.");

        Problem selected = problems.get(new Random().nextInt(problems.size()));

        Passage passage = null;
        if ("국어".equals(subject) && selected.getPassageId() != null) {
            passage = passageRepository.findById(selected.getPassageId()).orElse(null);
        }

        return QuizProblemResponse.from(selected, passage);
    }

    private double[] mapLevelToCorrectRate(String level) {
        return switch (level) {
            case "최하" -> new double[]{0, 20};
            case "하" -> new double[]{20, 40};
            case "중" -> new double[]{40, 60};
            case "상" -> new double[]{60, 80};
            case "최상" -> new double[]{80, 100};
            default -> throw new IllegalArgumentException("올바르지 않은 난이도");
        };
    }
}
