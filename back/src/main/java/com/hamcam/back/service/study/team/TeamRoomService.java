package com.hamcam.back.service.study.team;

import com.hamcam.back.dto.study.team.request.*;
import com.hamcam.back.dto.study.team.response.FocusRankingResponse;
import com.hamcam.back.dto.study.team.response.TeamRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostCategory;
import com.hamcam.back.entity.study.RoomStatus;
import com.hamcam.back.entity.study.TeamRoom;
import com.hamcam.back.entity.study.TeamRoomMode;
import com.hamcam.back.entity.study.Vote;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.study.TeamRoomRepository;
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

    /**
     * 문제풀이방 생성
     * - 요청자의 유저 ID로 사용자 조회 후, QUIZ 모드 팀방 생성
     */
    /**
     * 문제풀이방 생성
     * - QUIZ 모드 팀방 생성
     * - 최소 인원수 조건 및 유효성 체크 포함
     */
    public TeamRoomResponse createQuizRoom(TeamRoomCreateRequest request) {
        User creator = getUser(request.getUserId());

        if (request.getTargetTime() == null || request.getTargetTime() < 10) {
            throw new CustomException(ErrorCode.INVALID_TIME_VALUE);
        }

        TeamRoom room = TeamRoom.builder()
                .mode(TeamRoomMode.QUIZ)
                .creator(creator)
                .roomName(request.getRoomName()) // ✅ 필드명과 일치해야 함
                .password(request.getPassword())
                .targetTime(request.getTargetTime())
                .status(RoomStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        teamRoomRepository.save(room);
        return TeamRoomResponse.from(room);
    }

    /**
     * 공부시간 경쟁방 생성
     * - 최소 인원 조건(3명 이상 참여 유도) 및 목표 시간 설정
     */
    public TeamRoomResponse createFocusRoom(TeamRoomCreateRequest request) {
        User creator = getUser(request.getUserId());

        if (request.getTargetTime() == null || request.getTargetTime() < 10) {
            throw new CustomException(ErrorCode.INVALID_TIME_VALUE);
        }

        TeamRoom room = TeamRoom.builder()
                .mode(TeamRoomMode.FOCUS)
                .creator(creator)
                .roomName(request.getRoomName())  // ✅ 필드명과 일치하도록 수정
                .password(request.getPassword())
                .targetTime(request.getTargetTime())
                .status(RoomStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        teamRoomRepository.save(room);
        return TeamRoomResponse.from(room);
    }

    /**
     * 방 상세 조회
     * - 존재하지 않으면 예외 처리
     */
    public TeamRoomResponse getTeamRoomById(TeamRoomDetailRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        return TeamRoomResponse.from(room);
    }

    /**
     * 전체 팀방 목록 조회
     * - mode 별 필터링 확장 가능
     */
    public List<TeamRoomResponse> getAllTeamRooms(TeamRoomListRequest request) {
        List<TeamRoom> rooms = teamRoomRepository.findAll();

        // 추후 mode, status 등에 따른 필터링 로직 추가 가능
        return rooms.stream()
                .map(TeamRoomResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 팀방 입장 시 비밀번호 확인
     * - 비밀번호가 없으면 true
     * - 있으면 일치 여부 검사
     */
    public boolean checkRoomPassword(TeamRoomPasswordRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());

        String savedPassword = room.getPassword();
        String inputPassword = request.getPassword();

        if (savedPassword == null || savedPassword.isBlank()) {
            return true; // 공개방
        }

        if (inputPassword == null || !savedPassword.equals(inputPassword)) {
            return false;
        }

        return true;
    }

    /**
     * 문제풀이 시작 요청 (방장만 가능)
     * - 상태를 QUIZ_IN_PROGRESS로 전환
     */
    public void startQuizSession(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(request.getUserId(), room);

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new CustomException(ErrorCode.ALREADY_STARTED);
        }

        room.setStatus(RoomStatus.QUIZ_IN_PROGRESS);
        room.setQuizStartedAt(LocalDateTime.now());
        room.setCurrentQuestionIndex(0); // 문제 인덱스 초기화
        teamRoomRepository.save(room);
    }

    /**
     * 손들기 요청
     * - 이미 손을 든 유저는 중복 요청 방지
     */
    public void raiseHand(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        Long userId = request.getUserId();

        if (room.getRaisedHands().contains(userId)) {
            throw new CustomException(ErrorCode.ALREADY_RAISED_HAND);
        }

        room.getRaisedHands().add(userId);
        teamRoomRepository.save(room);
    }

    /**
     * 발표 종료 처리
     * - 현재 발표자 정보 제거 및 손든 목록 초기화
     */
    public void endPresentation(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(request.getUserId(), room);

        room.setCurrentPresenterId(null);
        room.getRaisedHands().clear();
        teamRoomRepository.save(room);
    }

    /**
     * 투표 제출
     * - 발표자에 대한 점수 등록
     */
    public void submitVote(TeamRoomVoteRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        Long presenterId = request.getTargetUserId();
        Integer score = request.getScore();

        if (score < 1 || score > 5) {
            throw new CustomException(ErrorCode.INVALID_VOTE_SCORE);
        }

        Vote vote = Vote.builder()
                .presenterId(presenterId)
                .score(score)
                .build();

        room.addVote(vote); // ✅ TeamRoom 내 리스트에 vote 추가
        teamRoomRepository.save(room); // vote도 cascade 저장
    }


    /**
     * 다음 문제로 진행
     * - 방장만 가능, 문제 인덱스 증가
     */
    public void moveToNextQuestion(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(request.getUserId(), room);

        if (room.getStatus() != RoomStatus.QUIZ_IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
        }

        room.setCurrentQuestionIndex(room.getCurrentQuestionIndex() + 1);
        teamRoomRepository.save(room);
    }

    /**
     * 문제풀이 종료 처리
     * - 방장만 가능
     * - 방 상태를 종료로 변경
     */
    public void endQuizRoom(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(request.getUserId(), room);

        if (room.getStatus() != RoomStatus.QUIZ_IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
        }

        room.setStatus(RoomStatus.QUIZ_ENDED);
        teamRoomRepository.save(room);
    }


    /**
     * 질문 게시글 등록
     * - 실패한 문제를 커뮤니티 질문 게시글로 전환
     */
    public void postQuestionToCommunity(TeamRoomUnsolvedPostRequest request) {
        User user = getUser(request.getUserId());
        TeamRoom room = getRoomOrThrow(request.getRoomId());

        Post post = Post.builder()
                .writer(user)
                .title("[질문] " + request.getTitle())
                .content(request.getContent())
                .category(PostCategory.QUESTION)
                .teamRoom(room)
                .build();

        postRepository.save(post);
    }



    /**
     * 공부시간 경쟁 시작
     * - 시작 시간 기록
     */
    public void startFocusTimer(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        Long userId = request.getUserId();

        // Redis 또는 DB에 시작 시간 기록 (studyTimeService 사용 예시)
        studyTimeService.recordStartTime(room.getId(), userId, LocalDateTime.now());
    }


    /**
     * 실시간 공부시간 업데이트
     * - 누적 시간 저장 처리 (Redis 또는 DB)
     */
    public void updateStudyTime(TeamRoomStudyTimeRequest request) {
        Long roomId = request.getRoomId();
        Long userId = request.getUserId();
        int studyMinutes = request.getStudyMinutes();

        if (studyMinutes <= 0) {
            throw new CustomException(ErrorCode.INVALID_TIME_VALUE);
        }

        // 누적 시간 업데이트
        studyTimeService.addStudyTime(roomId, userId, studyMinutes);
    }


    /**
     * 목표 공부시간 달성 처리
     * - 최초 도달 유저에게 포인트 지급
     * - 방 종료 처리
     */
    public void completeFocusSession(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        Long userId = request.getUserId();

        int totalMinutes = studyTimeService.getTotalMinutes(room.getId(), userId);
        if (totalMinutes < room.getTargetTime()) {
            throw new CustomException(ErrorCode.TARGET_TIME_NOT_REACHED);
        }

        if (room.getWinnerId() != null) {
            // 이미 목표 달성한 유저가 있음
            return;
        }

        // 승자 등록 및 포인트 지급
        room.setWinnerId(userId);
        room.setStatus(RoomStatus.FOCUS_COMPLETE);
        teamRoomRepository.save(room);

        pointService.grantPoint(userId, 100); // 예시 포인트 지급
    }


    /**
     * 공부시간 경쟁 종료 후 랭킹 반환
     * - 모든 참여자의 공부 시간 기준으로 정렬된 랭킹 반환
     * - 방 삭제 처리 (방장만 가능)
     */
    public FocusRankingResponse finalizeFocusRoom(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(request.getUserId(), room);

        // 1. 참여자들의 공부 시간 조회
        Map<Long, Integer> studyTimeMap = studyTimeService.getAllUserStudyTimes(room.getId()); // userId -> minutes

        // 2. 정렬 및 랭킹 생성
        List<FocusRankingResponse.Rank> ranks = studyTimeMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(entry -> {
                    User user = getUser(entry.getKey());
                    return new FocusRankingResponse.Rank(user.getNickname(), entry.getValue());
                })
                .collect(Collectors.toList());

        // 3. 방 삭제
        teamRoomRepository.delete(room);

        // 4. 랭킹 응답 생성
        return FocusRankingResponse.builder()
                .roomName(room.getRoomName())  // ✅ 필드명 일치
                .ranks(ranks)
                .build();
    }


    /**
     * 방 삭제
     * - 방장만 가능
     */
    public void deleteRoom(TeamRoomUserRequest request) {
        TeamRoom room = getRoomOrThrow(request.getRoomId());
        validateHost(request.getUserId(), room);

        teamRoomRepository.delete(room);
    }


    /**
     * 유저 조회
     * - 존재하지 않으면 예외 발생
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


    /**
     * 방 조회
     * - 존재하지 않으면 예외 발생
     */
    private TeamRoom getRoomOrThrow(Long roomId) {
        return teamRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }


    /**
     * 방장 권한 확인
     * - 요청한 유저가 방 생성자인지 확인
     */
    private void validateHost(Long userId, TeamRoom room) {
        if (!room.getCreator().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_ROOM_HOST);
        }
    }



}
