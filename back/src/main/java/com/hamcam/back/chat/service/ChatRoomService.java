package com.hamcam.back.chat.service;

/**
 * ChatRoomService
 *
 * 실시간 채팅방 관련 핵심 로직을 처리하는 서비스 클래스
 * 사용자가 채팅방을 생성하거나 입장하고, 목록을 조회하는 기능을 제공하며,
 * WebSocket과 HTTP API 모두에서 공통으로 사용됨
 *
 * [연관 테이블]
 * CHAT_ROOMS -> 채팅방의 기본 정보 (방 ID, 타입, 연동 ID, 생성일 등) 저장
 *
 * [주요 기능 설명]
 * -> 채팅방 생성
 * -> 전체 채팅방 목록 조회
 * -> 특정 채팅방 상세 조회
 * -> 채팅방 입장 / 퇴장 처리 (세션 기반 참여 가능성)
 */
public class ChatRoomService {
}
