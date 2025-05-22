package com.hamcam.back.service.study;

import com.hamcam.back.dto.study.team.request.TeamRoomCreateRequest;
import com.hamcam.back.dto.study.team.response.TeamRoomResponse;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.TeamRoom;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.repository.study.TeamRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [TeamRoomService]
 *
 * 팀 학습방 생성, 조회, 목록 반환 서비스
 */
@Service
@RequiredArgsConstructor
public class TeamRoomService {

    private final TeamRoomRepository teamRoomRepository;
    private final UserRepository userRepository;

    /**
     * 팀 학습방 생성
     *
     * @param userId  사용자 ID
     * @param request 학습방 생성 요청
     * @return 생성된 학습방 정보
     */
    public TeamRoomResponse createTeamRoom(Long userId, TeamRoomCreateRequest request) {
        User host = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        TeamRoom room = TeamRoom.builder()
                .title(request.getTitle())
                .roomType(request.getRoomType())
                .maxParticipants(request.getMaxParticipants())
                .password(request.getPassword() != null && !request.getPassword().isBlank()
                        ? request.getPassword()
                        : null)
                .host(host)
                .build();

        TeamRoom saved = teamRoomRepository.save(room);
        return TeamRoomResponse.from(saved);
    }

    /**
     * 학습방 단건 조회
     *
     * @param id 학습방 ID
     * @return 학습방 정보
     */
    public TeamRoomResponse getTeamRoomById(Long id) {
        TeamRoom room = teamRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_ROOM_NOT_FOUND));
        return TeamRoomResponse.from(room);
    }

    /**
     * 전체 학습방 목록 조회
     *
     * @return 학습방 목록
     */
    public List<TeamRoomResponse> getAllTeamRooms() {
        return teamRoomRepository.findAll().stream()
                .map(TeamRoomResponse::from)
                .collect(Collectors.toList());
    }
}
