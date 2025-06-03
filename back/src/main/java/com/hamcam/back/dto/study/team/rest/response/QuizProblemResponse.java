package com.hamcam.back.dto.study.team.rest.response;

import com.hamcam.back.entity.study.problem.Passage;
import com.hamcam.back.entity.study.problem.Problem;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class QuizProblemResponse {
    private Long problemId;
    private String subject;
    private String title;
    private String imagePath;
    private List<String> choices;
    private PassageContent passage;

    public static QuizProblemResponse from(Problem problem, Passage passage) {
        return QuizProblemResponse.builder()
                .problemId(problem.getProblemId())
                .subject(problem.getSubject())
                .title(problem.getSource() + " 문제")
                .imagePath(problem.getImagePath())
                .choices(parseChoices(problem.getAnswer())) // 실제 정답이 아닌 보기 목록이라면 다른 필드 필요
                .passage(passage != null ? new PassageContent(passage.getTitle(), passage.getContent()) : null)
                .build();
    }

    private static List<String> parseChoices(String raw) {
        return Arrays.asList("①", "②", "③", "④", "⑤"); // 추후 실제 choice 컬럼 생기면 교체
    }

    @Getter
    @Builder
    public static class PassageContent {
        private String title;
        private String content;
    }
}
