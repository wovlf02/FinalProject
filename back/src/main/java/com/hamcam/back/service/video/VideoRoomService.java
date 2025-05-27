package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.request.CreateRoomRequest;
import com.hamcam.back.dto.video.request.JoinRoomRequest;
import com.hamcam.back.dto.video.response.VideoRoomInfoResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.video.Participant;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.video.ParticipantRepository;
import com.hamcam.back.repository.video.VideoRoomRepository;
import com.hamcam.back.util.InviteCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ WebRTC 기반 팀 학습방 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    /**
     * ✅ 팀 학습방 생성
     */
    public VideoRoomInfoResponse createRoom(CreateRoomRequest request, Long userId) {
        User host = getUser(userId);

        VideoRoom room = VideoRoom.builder()
                .title(request.getTitle())
                .password(request.getPassword())
                .maxParticipants(request.getMaxParticipants())
                .roomType(request.getRoomType())
                .inviteCode(InviteCodeGenerator.generate())
                .host(host)
                .build();

        videoRoomRepository.save(room);

        Participant participant = Participant.builder()
                .user(host)
                .room(room)
                .isPresenter(false)
                .focusTime(0)
                .build();

        participantRepository.save(participant);

        return VideoRoomInfoResponse.from(room);
    }

    /**
     * ✅ 팀 학습방 입장
     */
    public VideoRoomInfoResponse joinRoom(JoinRoomRequest request, Long userId) {
        User user = getUser(userId);
        VideoRoom room = findRoomByIdOrInviteCode(request.getRoomId(), request.getInviteCode());

        if (!room.isActive()) {
            throw new CustomException(ErrorCode.ROOM_ALREADY_CLOSED);
        }

        if (room.getPassword() != null && !room.getPassword().isBlank()) {
            if (!room.getPassword().equals(request.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
        }

        if (participantRepository.existsByUserIdAndRoomId(userId, room.getId())) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_ROOM);
        }

        if (room.getParticipants().size() >= room.getMaxParticipants()) {
            throw new CustomException(ErrorCode.ROOM_IS_FULL);
        }

        Participant participant = Participant.builder()
                .user(user)
                .room(room)
                .socketId(request.getSocketId())
                .isPresenter(false)
                .focusTime(0)
                .build();

        participantRepository.save(participant);

        return VideoRoomInfoResponse.from(room);
    }

    /**
     * ✅ 단일 방 정보 조회
     */
    public VideoRoomInfoResponse getRoomInfo(Long roomId) {
        VideoRoom room = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        return VideoRoomInfoResponse.from(room);
    }

    /**
     * ✅ 방 종료 (방장만 가능)
     */
    public void endRoom(Long roomId, Long userId) {
        VideoRoom room = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getHost().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        room.setActive(false);
        videoRoomRepository.save(room);
    }

    /**
     * ✅ 내가 속한 방 목록
     */
    public List<VideoRoomInfoResponse> getRoomsByUser(Long userId) {
        return participantRepository.findAllByUserId(userId).stream()
                .map(p -> VideoRoomInfoResponse.from(p.getRoom()))
                .filter(VideoRoomInfoResponse::isActive)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 전체 활성화 방 목록
     */
    public List<VideoRoomInfoResponse> getAllActiveRooms() {
        return videoRoomRepository.findByIsActiveTrue().stream()
                .map(VideoRoomInfoResponse::from)
                .collect(Collectors.toList());
    }

    // ==================== 내부 메서드 ====================

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private VideoRoom findRoomByIdOrInviteCode(Long roomId, String inviteCode) {
        if (roomId != null) {
            return videoRoomRepository.findById(roomId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        } else if (inviteCode != null && !inviteCode.isBlank()) {
            return videoRoomRepository.findByInviteCode(inviteCode)
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        } else {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }
}
