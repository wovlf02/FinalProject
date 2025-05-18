package com.hamcam.back.entity.chat;

/**
 * 채팅 메시지 타입을 정의하는 열거형입니다.
 * - TEXT: 일반 텍스트 메시지
 * - IMAGE: 이미지 첨부 메시지 (미리보기 가능)
 * - FILE: 일반 파일 첨부 메시지 (예: PDF, Word 등)
 */
public enum ChatMessageType {

    /**
     * 일반 텍스트 메시지
     */
    TEXT,

    /**
     * 이미지 파일 (jpg, png 등)
     */
    IMAGE,

    /**
     * 일반 파일 (PDF, Word, etc.)
     */
    FILE
}
