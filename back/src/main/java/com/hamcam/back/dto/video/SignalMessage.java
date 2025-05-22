package com.hamcam.back.dto.video;

import lombok.*;

/**
 * [SignalMessage]
 *
 * WebRTC Signaling 통신에 사용되는 WebSocket 메시지 DTO입니다.
 * - offer / answer / candidate / join / leave / start / chat 등의 메시지를 처리할 수 있습니다.
 * - 메시지 타입(type)에 따라 데이터 구조(data)는 달라질 수 있습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalMessage {

    /**
     * 메시지 타입
     * - 예: offer, answer, candidate, join, leave, chat, start 등
     */
    private String type;

    /**
     * 메시지를 보낸 사용자 ID (프론트에서 LocalStorage 기반으로 전달)
     */
    private Long userId;

    /**
     * 대상 방 ID (WebRTC 방 또는 팀 스터디방 ID 등)
     */
    private String roomId;

    /**
     * 전송 데이터 (SDP 정보 또는 ICE candidate 또는 채팅 메시지 등)
     */
    private String data;

    /**
     * 수신 대상 사용자 ID (필요 시 사용, 예: offer → 특정 사용자에게 전송)
     * → 없으면 전체 브로드캐스트
     */
    private Long targetUserId;
}
