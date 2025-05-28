package com.hamcam.back.service.study.team;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.*;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.StudyRoomParticipantRepository;
import com.hamcam.back.repository.study.StudyRoomRepository;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.util.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomLifecycleService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    /** ✅ 방 비활성화 처리 */
    public void deactivateRoom(Long roomId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.deactivate();
    }

    /** ✅ 방장 여부 확인 */
    public boolean isHost(Long roomId, Long userId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return participantRepository.existsByRoomAndUserAndIsHostTrue(room, user);
    }

    /** ✅ 최소 인원 수(3명) 충족 여부 확인 */
    public void checkMinParticipants(Long roomId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        int count = participantRepository.countByRoom(room);
        if (count < 3) {
            throw new CustomException(ErrorCode.MINIMUM_PARTICIPANT_NOT_MET);
        }
    }

    /** ✅ Redis 로그 정리 (채팅, 집중시간 등) */
    public void deleteRoomLogs(Long roomId) {
        redisService.deleteChatLog(roomId);
        redisService.deleteFocusRoomData(roomId); // focus:{roomId}:* 삭제
    }
}
