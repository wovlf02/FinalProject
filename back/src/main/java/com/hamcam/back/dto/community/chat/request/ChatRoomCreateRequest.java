package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long creatorId;
    private String roomName;
    private List<Long> invitedUserIds;
    @JsonIgnore
    private MultipartFile image;
}
