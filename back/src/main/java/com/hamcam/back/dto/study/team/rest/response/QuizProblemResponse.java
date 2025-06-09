package com.hamcam.back.dto.study.team.rest.response;

import com.hamcam.back.entity.study.team.Problem;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class QuizProblemResponse {

    private Long problemId;
    private String subject;
    private String title;       // 문제 제목 or 본문
    private List<String> choices;
    private String imagePath;   // 이미지 경로 (국어 외 과목용)
    private String passage;     // 국어 지문 (국어 전용)

    /**
     * ✅ Problem + 지문 → QuizProblemResponse 변환
     */
    public static QuizProblemResponse of(Problem problem, String passageText) {
        return QuizProblemResponse.builder()
                .problemId(problem.getProblemId())
                .subject(problem.getSubject())
                .title(problem.getUnit().getUnit()) // 또는 problem.getTitle() 필드가 있다면 교체
                .choices(parseChoices(problem.getAnswer())) // 임시 처리: 실제 choices 필드가 있다면 교체
                .imagePath(problem.getImagePath())
                .passage(passageText)
                .build();
    }

    /**
     * ✅ 선택지를 임시로 파싱하는 유틸 (ex. "① ... / ② ... / ③ ...")
     * 추후 DB에 choices 컬럼이 분리되어 있으면 교체 필요
     */
    private static List<String> parseChoices(String answer) {
        // TODO: 임시 로직 (답안이 아닌 choices를 따로 저장하도록 추후 수정 필요)
        return Arrays.asList("① 보기1", "② 보기2", "③ 보기3", "④ 보기4", "⑤ 보기5");
    }
}
