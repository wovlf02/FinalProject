package com.hamcam.back.dto.video.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ SFU signaling 메시지 DTO
 * - offer, answer, candidate 등
 */
@Getter
@Setter
@NoArgsConstructor
public class SignalMessage {

    /** signaling 메시지 타입 (offer, answer, candidate) */
    private String type;

    /** 보낸 유저의 socketId */
    private String from;

    /** 받는 유저의 socketId */
    private String to;

    /** 방 ID */
    private Long roomId;

    /** SDP 또는 ICE candidate 정보 */
    private Object data;
}
