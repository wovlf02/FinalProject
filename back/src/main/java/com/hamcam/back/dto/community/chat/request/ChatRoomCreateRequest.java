package com.hamcam.back.dto.community.chat.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * [ChatRoomCreateRequest]
 *
 * 채팅방 생성 요청 DTO
 * 사용자는 채팅방 이름, 초대할 사용자 ID 리스트, 대표 이미지를 포함하여 채팅방을 생성할 수 있습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomCreateRequest {

    /**
     * 생성할 채팅방 이름
     */
    @NotBlank(message = "채팅방 이름은 필수 입력 값입니다.")
    private String roomName;

    /**
     * 채팅방에 초대할 사용자 ID 목록 (자기 자신 제외)
     */
    @NotEmpty(message = "최소 1명 이상의 초대 대상이 필요합니다.")
    private List<Long> invitedUserIds;

    /**
     * 채팅방 대표 이미지 (선택)
     */
    private MultipartFile image;
}
