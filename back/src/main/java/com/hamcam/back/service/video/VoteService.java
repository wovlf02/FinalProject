package com.hamcam.back.service.video;

import com.hamcam.back.dto.video.request.VoteRequest;
import com.hamcam.back.dto.video.response.VoteResultResponse;
import com.hamcam.back.dto.video.response.inner.VoteSummary;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.video.Participant;
import com.hamcam.back.entity.video.Presentation;
import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.entity.video.Vote;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.video.ParticipantRepository;
import com.hamcam.back.repository.video.PresentationRepository;
import com.hamcam.back.repository.video.VideoRoomRepository;
import com.hamcam.back.repository.video.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ✅ 발표자 투표 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final ParticipantRepository participantRepository;
    private final VideoRoomRepository videoRoomRepository;
    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;

    /**
     * ✅ 투표 제출
     */
    public void submitVote(Long voterId, VoteRequest request) {
        Long roomId = request.getRoomId();
        Long targetUserId = request.getTargetUserId();

        // 중복 투표 방지
        if (voteRepository.existsByRoomIdAndTargetUserIdAndVoterId(roomId, targetUserId, voterId)) {
            throw new CustomException(ErrorCode.DUPLICATE_VOTE);
        }

        // 유효성 확인
        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User presenter = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        VideoRoom room = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        Vote vote = Vote.builder()
                .voter(voter)
                .targetUser(presenter)
                .room(room)
                .agree(request.isAgree())
                .build();

        voteRepository.save(vote);
    }

    /**
     * ✅ 투표 결과 계산 + 발표 기록 저장
     */
    public VoteResultResponse calculateVoteResult(Long roomId, Long presenterId) {
        List<Vote> votes = voteRepository.findAllByRoomIdAndTargetUserId(roomId, presenterId);
        int agree = (int) votes.stream().filter(Vote::isAgree).count();
        int disagree = votes.size() - agree;
        boolean passed = agree > votes.size() / 2;

        // 발표 성공 시 기록 저장
        if (passed) {
            User presenter = userRepository.findById(presenterId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            VideoRoom room = videoRoomRepository.findById(roomId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

            Presentation presentation = Presentation.builder()
                    .room(room)
                    .presenter(presenter)
                    .passed(true)
                    .build();
            presentationRepository.save(presentation);

            // ✅ 포인트 지급은 여기서 별도 서비스로 분리 가능
            // pointService.givePoint(presenterId, 발표성공포인트);
        }

        // ✅ 투표 결과 반환
        return VoteResultResponse.builder()
                .roomId(roomId)
                .result(
                        VoteSummary.builder()
                                .targetUserId(presenterId)
                                .agreeCount(agree)
                                .disagreeCount(disagree)
                                .passed(passed)
                                .build()
                )
                .build();
    }
}
