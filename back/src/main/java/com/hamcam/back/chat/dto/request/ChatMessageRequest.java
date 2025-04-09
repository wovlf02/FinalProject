package com.hamcam.back.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * [ChatMessageRequest]
 *
 * WebSocket을 통해 클라이언트 -> 서버로 전송되는 실시간 채팅 메시지 요청 DTO
 * 텍스트 메시지 또는 첨부파일(이미지, 문서 등)을 포함
 *
 * [사용 경로]
 * MessageMapping("/chat/message")
 * -> 클라이언트는 해당 엔드포인트로 메시지를 발송하고, 서버는 수신 후 /sub/chat/room/{roomId}로 브로드캐스팅함
 *
 * [연관 테이블]
 * CHAT_MESSAGES 테이블과 매핑되어 메시지를 저장할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequest {

    /**
     * 채팅방 고유 ID
     *
     * 필수 필드
     * 메시지를 어느 채팅방에 전송할지 식별
     * CHAT_MESSAGES.room_id 외래키로 저장
     */
    private Long roomId;

    /**
     * 메시지 발신자 ID
     *
     * 필수 필드
     * USERS 테이블의 user_id와 연결
     * 메시지를 누가 보냈는지 식별
     */
    private Long senderId;

    /**
     * 채팅 텍스트 메시지 내용
     *
     * 선택 필드
     * 텍스트 또는 파일 중 최소 하나는 존재해야 함
     * HTML/스크립트 공격 방지를 위한 필터링 필요
     */
    private String content;

    /**
     * 첨부파일
     *
     * 선택 필드
     * 이미지, 문서 등의 첨부파일
     * 바이너리 파일은 서버에서 BLOB 형태로 처리됨
     * S3 등 외부 저장소를 사용하는 경우 링크로 처리할 수도 있음
     */
    private MultipartFile file;

    /**
     * 첨부파일의 MIME 타입
     *
     * ex) image/jpeg, application/pdf
     * 저장 및 클라이언트 렌더링에 사용
     */
    private String fileType;
}
