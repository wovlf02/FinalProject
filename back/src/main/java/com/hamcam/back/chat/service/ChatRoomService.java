package com.hamcam.back.chat.service;

import com.hamcam.back.chat.dto.request.ChatJoinRequest;
import com.hamcam.back.chat.dto.request.ChatRoomCreateRequest;
import com.hamcam.back.chat.dto.response.ChatRoomListResponse;
import com.hamcam.back.chat.dto.response.ChatRoomResponse;
import com.hamcam.back.chat.entity.ChatRoom;
import com.hamcam.back.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * [채팅방 생성]
     *
     * 사용자가 게시글/그룹/스터디 등과 연동된 새로운 채팅방을 만들고자 할 때 호출됨
     * 요청 데이터에 따라 CHAT_ROOMS 테이블에 레코드를 저장하고,
     * 채팅방 고유 ID, 타입, 생성 시간 등을 응답 객체로 반환함
     *
     * @param request ChatRoomCreateRequest -> roomType과 referenceId를 포함하는 생성 요청 DTO
     * @return ChatRoomResponse -> 생성된 채팅방의 상세 정보 및 메시지
     */
    @Transactional
    public ChatRoomResponse createRoom(ChatRoomCreateRequest request) {
        ChatRoom room = ChatRoom.builder()
                .roomType(request.getRoomType()) // 게시글 기반(post), 그룹 기반(group) 등
                .referenceId(request.getReferenceId()) // 연결된 외부 리소스 ID (ex. 게시글 ID)
                .createdAt(LocalDateTime.now()) // 생성 시각 기록
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(room); // DB 저장

        return ChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .roomType(savedRoom.getRoomType())
                .referenceId(savedRoom.getReferenceId())
                .createdAt(savedRoom.getCreatedAt())
                .message("채팅방이 성공적으로 생성되었습니다.")
                .build();
    }

    /**
     * [전체 채팅방 목록 조회]
     *
     * 관리 목적 또는 클라이언트에서 여러 채팅방에 대한 목록을 불러올 때 사용됨
     * CHAT_ROOMS 테이블에 존재하는 모든채팅방 레코드를 조회하여 리스트 형태로 반환
     *
     * @return List<ChatRoomListResponse> -> 각 채팅방의 요약 정보(방 ID, 타입, 생성일 등)
     */
    @Transactional
    public List<ChatRoomListResponse> getAllRooms() {
        return chatRoomRepository.findAll()
                .stream()
                .map(room -> ChatRoomListResponse.builder()
                        .roomId(room.getId())
                        .roomType(room.getRoomType())
                        .referenceId(room.getReferenceId())
                        .createdAt(room.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * [특정 채팅방 상세 조회]
     *
     * 클라이언트에서 특정 채팅방에 입장하거나 상세 정보를 확인할 때 사용됨
     * roomId를 기준으로 CHAT_ROOMS 테이블에서 조회하며, 해당 정보가 없을 경우 예외 발생
     *
     * @param roomId Long -> 조회할 채팅방의 고유 식별자 (PK)
     * @return ChatRoomResponse -> 채팅방 상세 정보와 안내 메시지 포함
     */
    @Transactional
    public ChatRoomResponse getRoomDetail(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .roomType(room.getRoomType())
                .referenceId(room.getReferenceId())
                .createdAt(room.getCreatedAt())
                .message("채팅방 상세 정보를 불러왔습니다.")
                .build();
    }

    /**
     * [채팅방 입장]
     *
     * 사용자가 채팅방에 입장 요청을 보낼 때 실행됨
     * DB에 입장 여부를 저장하진 않지만, 유효한 채팅방인지 검증하여 입장 가능 여부 판단
     *
     * @param roomId Long -> 입장할 채팅방 ID
     * @param request ChatJoinRequest -> 사용자 ID, 닉네임 등의 정보 포함
     */
    @Transactional
    public void joinRoom(Long roomId, ChatJoinRequest request) {
        boolean exists = chatRoomRepository.existsById(roomId);

        if(!exists) {
            throw new IllegalArgumentException("입장하려는 채팅방이 존재하지 않습니다.");
        }
        // 추후 WebSocket 세션 또는 참여자 기록 저장 로직을 확장 가능
    }

    /**
     * [채팅방 퇴장]
     *
     * 사용자가 채팅방에서 나갈 때 호출됨
     * DB에 직접 기록은 없지만 퇴장 처리를 통해 실시간 통신 종료 등을 유도 가능
     * @param roomId
     * @param request
     */
    @Transactional
    public void exitRoom(Long roomId, ChatJoinRequest request) {
        boolean exists = chatRoomRepository.existsById(roomId);
        if(!exists) {
            throw new IllegalArgumentException("퇴장하려는 채팅방이 존재하지 않습니다.");
        }
        // 필요 시 WebSocket disconnect 또는 참여자 명단에서 제거
    }
}
