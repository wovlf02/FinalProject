package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * [ChatRoomCreateRequest]
 * 채팅방 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomCreateRequest {

    @NotNull(message = "생성자 ID는 필수입니다.")
    private Long creatorId;

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    private String roomName;

    @NotEmpty(message = "초대할 사용자 ID 리스트는 비어 있을 수 없습니다.")
    private List<Long> invitedUserIds;

    /**
     * 대표 이미지 (선택)
     */
    @JsonIgnore
    private MultipartFile image;
}
