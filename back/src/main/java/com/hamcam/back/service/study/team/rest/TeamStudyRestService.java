package com.hamcam.back.service.study.team.rest;

import com.hamcam.back.dto.study.team.rest.request.*;
import com.hamcam.back.dto.study.team.rest.response.*;
import com.hamcam.back.dto.study.team.response.inner.ParticipantInfo;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.community.Post;
import com.hamcam.back.entity.community.PostCategory;
import com.hamcam.back.entity.study.team.*;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.community.post.PostRepository;
import com.hamcam.back.repository.study.*;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.service.util.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamStudyRestService {

    private final StudyRoomRepository studyRoomRepository;
    private final FocusRoomRepository focusRoomRepository;
    private final QuizRoomRepository quizRoomRepository;
    private final StudyRoomParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final ProblemRepository problemRepository;

    /**
     * ✅ 팀방 생성 (문제풀이방 or 공부시간 경쟁방)
     */
    public Long createRoom(TeamRoomCreateRequest request, Long userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String inviteCode = generateInviteCode();
        System.out.println("RoomType: " + request.getRoomType());

        StudyRoom room;

        if (request.getRoomType() == RoomType.QUIZ) {
            room = QuizRoom.builder()
                    .title(request.getTitle())
                    .password(request.getPassword())
                    .inviteCode(inviteCode)
                    .problemId(request.getProblemId())
                    .subject(request.getSubject())
                    .grade(request.getGrade())
                    .month(request.getMonth())
                    .difficulty(request.getDifficulty())
                    .host(user) // ✅ 필수 추가
                    .build();
            quizRoomRepository.save((QuizRoom) room);

        } else if (request.getRoomType() == RoomType.FOCUS) {
            room = FocusRoom.builder()
                    .title(request.getTitle())
                    .password(request.getPassword())
                    .inviteCode(inviteCode)
                    .targetTime(request.getTargetTime())
                    .host(user) // ✅ 필수 추가
                    .build();
            focusRoomRepository.save((FocusRoom) room);

        } else {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE);
        }

        // ✅ 생성자 참여 등록
        StudyRoomParticipant participant = StudyRoomParticipant.builder()
                .user(user)
                .studyRoom(room)
                .build();
        participantRepository.save(participant);

        return room.getId();
    }

    /**
     * ✅ 전체 팀방 목록 조회 (isActive=true 기준)
     */
    public List<TeamRoomSimpleInfo> getAllRooms(Long userId) {
        return studyRoomRepository.findAllByIsActiveTrue().stream()
                .map(TeamRoomSimpleInfo::from)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 유형(QUIZ / FOCUS) 팀방 목록 조회
     */
    public List<TeamRoomSimpleInfo> getRoomsByType(Long userId, String roomType) {
        RoomType type;
        try {
            type = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE);
        }

        return studyRoomRepository.findByRoomTypeAndIsActiveTrue(type).stream()
                .map(TeamRoomSimpleInfo::from)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 내가 속한 팀방 중 특정 유형 필터링
     */
    public List<TeamRoomSimpleInfo> getMyRoomsByType(Long userId, String roomType) {
        RoomType type;
        try {
            type = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE);
        }

        return participantRepository.findByUserId(userId).stream()
                .map(StudyRoomParticipant::getStudyRoom)
                .filter(room -> room.getRoomType() == type)
                .map(TeamRoomSimpleInfo::from)
                .collect(Collectors.toList());
    }




    /**
     * ✅ 내가 참여한 팀방 목록 조회
     */
    public List<TeamRoomSimpleInfo> getMyRooms(Long userId) {
        return participantRepository.findByUserId(userId).stream()
                .map(StudyRoomParticipant::getStudyRoom)
                .map(TeamRoomSimpleInfo::from)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 팀방 상세 정보 조회
     */
    public TeamRoomDetailResponse getRoomDetail(Long roomId, Long userId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        List<ParticipantInfo> participants = participantRepository.findByStudyRoomId(roomId).stream()
                .map(p -> ParticipantInfo.from(p.getUser()))
                .collect(Collectors.toList());

        TeamRoomDetailResponse.TeamRoomDetailResponseBuilder builder = TeamRoomDetailResponse.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .isActive(room.isActive())
                .inviteCode(room.getInviteCode())
                .password(room.getPassword())
                .participants(participants);

        if (room instanceof FocusRoom focusRoom) {
            builder.targetTime(focusRoom.getTargetTime());
        } else if (room instanceof QuizRoom quizRoom) {
            builder.problemId(quizRoom.getProblemId())
                    .subject(quizRoom.getSubject())
                    .grade(quizRoom.getGrade())
                    .month(quizRoom.getMonth())
                    .difficulty(quizRoom.getDifficulty());
        }

        return builder.build();
    }

    /**
     * ✅ 파일 업로드 (팀 학습용 - 로컬 저장)
     */
    public String saveFile(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return fileService.saveStudyFile(file, userId);
    }

    /**
     * ✅ 실패한 문제 커뮤니티에 자동 업로드
     */
    public void postFailureToCommunity(TeamRoomPostFailureRequest request, Long userId) {
        User user = getUser(userId);

        // 제목 및 내용 구성 (템플릿 방식)
        String title = "[질문] " + request.getProblemTitle();

        StringBuilder content = new StringBuilder();
        content.append("### ❗ 풀이 실패 문제\n");
        content.append("- 문제 제목: ").append(request.getProblemTitle()).append("\n");
        content.append("- 출처: ").append(request.getSource() != null ? request.getSource() : "미상").append("\n");
        content.append("- 분류: 질문\n\n");

        content.append("### 📌 질문 내용\n");
        content.append("Hamcam 팀 학습 중 해당 문제에 대한 풀이에 실패했습니다.\n");
        content.append("다른 사람들의 풀이 방법이나 접근 방식을 공유해주시면 감사하겠습니다.\n");

        // 게시글 생성 및 저장
        Post post = Post.builder()
                .writer(user)
                .title(title)
                .content(content.toString())
                .category(PostCategory.QUESTION)
                .tag("질문") // 기본 태그 설정 (필요시 request.getTag() 확장 가능)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }


    // ✅ 유저 조회 공통 유틸
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // ✅ 초대코드 생성기
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public void deleteRoom(Long roomId, Long userId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getHost().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_HOST);
        }

        // 1. 참가자 먼저 삭제
        participantRepository.deleteAllByStudyRoomId(roomId);

        // 2. 하드 삭제 (상속 구조 고려)
        if (room instanceof QuizRoom) {
            quizRoomRepository.deleteById(roomId);
        } else if (room instanceof FocusRoom) {
            focusRoomRepository.deleteById(roomId);
        } else {
            throw new CustomException(ErrorCode.INVALID_ROOM_TYPE);
        }
    }


    public void enterRoom(Long roomId, Long userId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        boolean alreadyExists = participantRepository.existsByStudyRoomIdAndUserId(roomId, userId);
        if (!alreadyExists) {
            User user = getUser(userId);
            StudyRoomParticipant participant = StudyRoomParticipant.builder()
                    .user(user)
                    .studyRoom(room)
                    .build();
            participantRepository.save(participant);
        }
    }

    public List<String> getUploadedFiles(Long roomId) {
        // 파일 저장 경로 기준으로 roomId 폴더가 존재한다는 가정
        return fileService.getStudyFileList(roomId);
    }


    public List<ProblemSummary> getFilteredProblems(String subject, Integer grade, Integer month, String difficulty) {
        List<QuizRoom> quizRooms = quizRoomRepository.findBySubjectAndGradeAndMonthAndDifficulty(subject, grade, month, difficulty);

        return quizRooms.stream()
                .map(QuizRoom::getProblemId)
                .distinct()
                .map(problemId -> problemRepository.findById(problemId).orElse(null))
                .filter(Objects::nonNull)
                .map(problem -> new ProblemSummary(
                        problem.getId(),
                        problem.getPassage() != null ? problem.getPassage().getTitle() : "(제목 없음)",
                        problem.getSubject(),
                        difficulty // QuizRoom 기준이므로 그대로 전달
                ))
                .collect(Collectors.toList());
    }




}
