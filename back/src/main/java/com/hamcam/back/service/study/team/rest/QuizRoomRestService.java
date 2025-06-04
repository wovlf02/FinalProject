package com.hamcam.back.service.study.team.rest;

import com.hamcam.back.dto.study.team.rest.response.QuizProblemResponse;
import com.hamcam.back.entity.study.team.Problem;
import com.hamcam.back.repository.study.ProblemRepository;
import com.hamcam.back.repository.study.PassageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizRoomRestService {

    private final ProblemRepository problemRepository;
    private final PassageRepository passageRepository;

    private final Random random = new Random();

    /**
     * ✅ 조건에 맞는 문제 중 하나를 랜덤하게 선택
     *
     * @param subject 과목
     * @param source 출처
     * @param level 난이도 (최하/하/중/상/최상)
     * @return 문제 + 지문(국어일 경우)
     */
    public QuizProblemResponse getRandomProblem(String subject, String source, String level) {
        int[] range = convertLevelToRateRange(level);
        double min = range[0];
        double max = range[1];

        List<Problem> problemList = problemRepository.findBySubjectAndSourceAndCorrectRateBetween(subject, source, min, max);
        if (problemList.isEmpty()) {
            throw new IllegalStateException("해당 조건의 문제가 존재하지 않습니다.");
        }

        Problem selected = problemList.get(random.nextInt(problemList.size()));

        String passage = null;
        if ("국어".equals(subject) && selected.getPassage() != null) {
            passage = selected.getPassage().getContent();
        }

        return QuizProblemResponse.of(selected, passage);
    }


    /**
     * ✅ 난이도 문자열을 정답률 범위로 변환
     */
    private int[] convertLevelToRateRange(String level) {
        return switch (level) {
            case "최하" -> new int[]{0, 20};
            case "하"   -> new int[]{20, 40};
            case "중"   -> new int[]{40, 60};
            case "상"   -> new int[]{60, 80};
            case "최상" -> new int[]{80, 100};
            default -> throw new IllegalArgumentException("잘못된 난이도 값: " + level);
        };
    }
}
