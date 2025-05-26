package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.request.*;
import com.hamcam.back.dto.study.team.response.FocusRankingResponse;
import com.hamcam.back.dto.study.team.response.TeamRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostCategory;
import com.hamcam.back.entity.study.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.study.TeamRoomRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamRoomService {

    private final TeamRoomRepository teamRoomRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final StudyTimeService studyTimeService;
    private final PointService pointService;

    // 퀴즈방 생성
    public TeamRoomResponse createQuizRoom(QuizRoomCreateRequest request, HttpServletRequest httpRequest) {
        User creator = getSessionUser(httpRequest); // 세션에서 userId를 얻고, DB에서 User를 반드시 조회

        // creator는 반드시 DB에 존재하는 User 객체여야 함
        TeamRoom room = TeamRoom.builder()
                .mode(request.getMode())
                .creator(creator)
                .roomName(request.getRoomName())
                .password(request.getPassword())
                .status(RoomStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        teamRoomRepository.save(room);
        return TeamRoomResponse.from(room);
    }

    // 집중방 생성
    public TeamRoomResponse createFocusRoom(FocusRoomCreateRequest request, HttpServletRequest httpRequest) {
        User creator = getSessionUser(httpRequest);
        validateTargetTime(request.getTargetTime());

        TeamRoom room = TeamRoom.builder()
                .mode(request.getMode())
                .creator(creator)
                .roomName(request.getRoomName())
                .password(request.getPassword())
                .targetTime(request.getTargetTime())
                .status(RoomStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        teamRoomRepository.save(room);
        return TeamRoomResponse.from(room);
    }

    // 방 상세 조회
    public TeamRoomResponse getTeamRoomById(TeamRoomDetailRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        return TeamRoomResponse.from(room);
    }

    // 전체 방 목록 조회
    public List<TeamRoomResponse> getAllTeamRooms(TeamRoomListRequest request) {
        return teamRoomRepository.findAll().stream()
                .map(TeamRoomResponse::from)
                .collect(Collectors.toList());
    }

    // 방 비밀번호 체크
    public boolean checkRoomPassword(TeamRoomPasswordRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        String savedPassword = room.getPassword();
        String inputPassword = request.getPassword();

        return savedPassword == null || savedPassword.isBlank() || savedPassword.equals(inputPassword);
    }

    // 퀴즈 세션 시작
    public void startQuizSession(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new CustomException(ErrorCode.ALREADY_STARTED);
        }

        room.setStatus(RoomStatus.QUIZ_IN_PROGRESS);
        room.setQuizStartedAt(LocalDateTime.now());
        room.setCurrentQuestionIndex(0);
        teamRoomRepository.save(room);
    }

    // 발표 종료
    public void endPresentation(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);

        room.setCurrentPresenterId(null);
        room.getRaisedHands().clear();
        teamRoomRepository.save(room);
    }

    // 투표 제출
    public void submitVote(TeamRoomVoteRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        if (request.getScore() < 1 || request.getScore() > 5) {
            throw new CustomException(ErrorCode.INVALID_VOTE_SCORE);
        }

        room.addVote(Vote.builder()
                .presenterId(request.getTargetUserId())
                .score(request.getScore())
                .build());

        teamRoomRepository.save(room);
    }

    // 다음 문제로 이동
    public void moveToNextQuestion(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);

        if (room.getStatus() != RoomStatus.QUIZ_IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
        }

        room.setCurrentQuestionIndex(room.getCurrentQuestionIndex() + 1);
        teamRoomRepository.save(room);
    }

    // 퀴즈방 종료
    public void endQuizRoom(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);

        if (room.getStatus() != RoomStatus.QUIZ_IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
        }

        room.setStatus(RoomStatus.QUIZ_ENDED);
        teamRoomRepository.save(room);
    }

    // 커뮤니티에 질문 게시
    public void postQuestionToCommunity(TeamRoomUnsolvedPostRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        TeamRoom room = getRoomOrThrow(request.getRoomId());

        Post post = Post.builder()
                .writer(user)
                .title("[질문] " + request.getAutoFilledTitle())
                .content(request.getQuestionContent())
                .category(PostCategory.QUESTION)
                .teamRoom(room)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    // 집중 타이머 시작
    public void startFocusTimer(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        studyTimeService.recordStartTime(request.getRoomId(), getSessionUser(httpRequest).getId(), LocalDateTime.now());
    }

    // 공부 시간 업데이트
    public void updateStudyTime(TeamRoomStudyTimeRequest request, HttpServletRequest httpRequest) {
        if (request.getStudyMinutes() <= 0) {
            throw new CustomException(ErrorCode.INVALID_TIME_VALUE);
        }
        studyTimeService.addStudyTime(request.getRoomId(), getSessionUser(httpRequest).getId(), request.getStudyMinutes());
    }

    // 집중 세션 완료
    public void completeFocusSession(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        Long userId = getSessionUser(httpRequest).getId();
        int totalMinutes = studyTimeService.getTotalMinutes(room.getId(), userId);

        if (totalMinutes < room.getTargetTime()) {
            throw new CustomException(ErrorCode.TARGET_TIME_NOT_REACHED);
        }
        if (room.getWinnerId() != null) return;

        room.setWinnerId(userId);
        room.setStatus(RoomStatus.FOCUS_COMPLETE);
        teamRoomRepository.save(room);
        pointService.grantPoint(httpRequest, 100);
    }

    // 집중방 랭킹 산출 및 방 삭제
    public FocusRankingResponse finalizeFocusRoom(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);

        Map<Long, Integer> studyTimeMap = studyTimeService.getAllUserStudyTimes(room.getId());
        List<FocusRankingResponse.Rank> ranks = studyTimeMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(entry -> new FocusRankingResponse.Rank(getUser(entry.getKey()).getNickname(), entry.getValue()))
                .collect(Collectors.toList());

        teamRoomRepository.delete(room);
        return FocusRankingResponse.builder()
                .roomName(room.getRoomName())
                .ranks(ranks)
                .build();
    }

    // 방 삭제
    public void deleteRoom(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);
        teamRoomRepository.delete(room);
    }

    // 집중 타겟 시간 검증
    private void validateTargetTime(Integer time) {
        if (time == null || time < 10) {
            throw new CustomException(ErrorCode.INVALID_TIME_VALUE);
        }
    }

    // 세션에서 userId를 가져와 DB에서 User를 반드시 조회
    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // roomId로 TeamRoom 조회
    private TeamRoom getRoomOrThrow(Long roomId) {
        return teamRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

    // userId로 User 조회
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 방장 검증
    private void validateHost(Long userId, TeamRoom room) {
        if (!room.getCreator().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_ROOM_HOST);
        }
    }

    // 퀴즈 세션 종료 (중복 메서드 정리)
    public void terminateQuizSession(TeamRoomUserRequest request, HttpServletRequest httpRequest) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(getSessionUser(httpRequest).getId(), room);

        if (room.getStatus() != RoomStatus.QUIZ_IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
        }

        room.setStatus(RoomStatus.QUIZ_ENDED);
        teamRoomRepository.save(room);
    }

    // 실패 문제 업로드 (중복 메서드 정리)
    public void uploadUnsolvedQuestionPost(TeamRoomUnsolvedPostRequest request, HttpServletRequest httpRequest) {
        User user = getSessionUser(httpRequest);
        TeamRoom room = getRoomOrThrow(request.getRoomId());

        Post post = Post.builder()
                .writer(user)
                .title("[실패문제] " + request.getAutoFilledTitle())
                .content(request.getQuestionContent())
                .category(PostCategory.QUESTION)
                .teamRoom(room)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }
}
