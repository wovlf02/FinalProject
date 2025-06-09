package com.hamcam.back.entity.chat;

/**
 * [ChatMessageType]
 * 채팅 메시지 타입 정의
 */
public enum ChatMessageType {
    TEXT,       // 일반 텍스트 메시지
    IMAGE,      // 이미지 파일
    FILE,       // 일반 파일
    READ_ACK,   // 읽음 확인
    ENTER;      // ✅ 사용자 입장 메시지

    /**
     * ✅ 미리보기 메시지로 사용할 수 있는 타입인지 여부
     */
    public boolean isPreviewType() {
        return this == TEXT || this == IMAGE || this == FILE;
    }
}
