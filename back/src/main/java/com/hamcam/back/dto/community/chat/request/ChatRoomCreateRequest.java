package com.hamcam.back.dto.community.chat.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 채팅방 생성 요청 DTO
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {

    /** 채팅방 이름 */
    private String roomName;

    /** 채팅방에 초대할 사용자 ID 목록 (자기 자신 제외) */
    private List<Long> invitedUserIds;

    // 채팅방 대표 이미지
    private MultipartFile image;
}
