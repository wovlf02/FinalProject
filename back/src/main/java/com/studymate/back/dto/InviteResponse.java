package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 채팅방 초대 응답 DTO
 * 초대 요청이 성공적으로 처리된 후 응답을 반환함
 */
@Getter
@Setter
@AllArgsConstructor
public class InviteResponse {

    /**
     * 초대된 사용자 ID 목록
     * 성공적으로 초대된 사용자들의 ID 리스트
     */
    private List<Long> invitedUserIds;

    /**
     * 실패한 사용자 ID 목록
     * 초대 실패한 사용자들의 ID 리스트
     */
    private List<Long> failedUserIds;
}
