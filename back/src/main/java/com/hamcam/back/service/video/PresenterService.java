package com.hamcam.back.service.video;

import com.hamcam.back.entity.video.Participant;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.video.ParticipantRepository;
import com.hamcam.back.repository.video.VideoRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ✅ 발표자 관리 서비스
 * - 손들기 → 발표자 선정
 * - 발표 종료 처리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PresenterService {

    private final ParticipantRepository participantRepository;
    private final VideoRoomRepository videoRoomRepository;

    /**
     * ✅ 발표자 등록 (손들기 → 발표자로 선정)
     */
    public void selectPresenter(Long userId, Long roomId) {
        VideoRoom room = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        List<Participant> participants = participantRepository.findAllByRoomId(roomId);

        // 이미 발표자가 존재하면 예외
        boolean hasPresenter = participants.stream().anyMatch(Participant::isPresenter);
        if (hasPresenter) {
            throw new CustomException(ErrorCode.ALREADY_STARTED); // 또는 ErrorCode.ALREADY_HAS_PRESENTER
        }

        // 해당 유저 참가자 찾기
        Participant participant = participants.stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        participant.setPresenter(true);
    }

    /**
     * ✅ 발표 종료 → 발표자 해제
     */
    public void endPresentation(Long roomId) {
        List<Participant> participants = participantRepository.findAllByRoomId(roomId);

        participants.stream()
                .filter(Participant::isPresenter)
                .findFirst()
                .ifPresent(p -> p.setPresenter(false));
    }

    /**
     * ✅ 현재 발표자 userId 조회
     */
    public Long getCurrentPresenter(Long roomId) {
        return participantRepository.findAllByRoomId(roomId).stream()
                .filter(Participant::isPresenter)
                .map(p -> p.getUser().getId())
                .findFirst()
                .orElse(null);
    }
}
