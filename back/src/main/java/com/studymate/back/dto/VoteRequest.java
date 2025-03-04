package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 채팅방 투표 생성 요청 DTO
 * 채팅방 내에서 사용자가 투표를 생성할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class VoteRequest {

    /**
     * 투표 주제
     * 사용자가 설정하는 투표 제목
     */
    @NotBlank(message = "투표 주제를 입력해야 합니다.")
    private String topic;

    /**
     * 투표 선택지 목록
     * 최소 2개 이상의 선택지가 필요함
     */
    @NotNull(message = "투표 선택지를 입력해야 합니다.")
    private List<String> options;
}
