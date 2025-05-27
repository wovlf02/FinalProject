package com.hamcam.back.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 🔐 인증 & 로그인 오류 (401)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E4011", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E4012", "토큰이 유효하지 않습니다."),
    LOGIN_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "E1001", "존재하지 않는 사용자입니다."),
    LOGIN_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "E1002", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "E4013", "인증되지 않은 사용자입니다."),

    // 🔒 권한 오류 (403)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E4031", "접근 권한이 없습니다."),
    NOT_ROOM_HOST(HttpStatus.FORBIDDEN, "E4032", "방장만 수행할 수 있는 작업입니다."),

    // ⚠️ 잘못된 요청 (400)
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E4001", "잘못된 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "E4002", "필수 파라미터가 누락되었습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "E4003", "파일 업로드에 실패했습니다."),
    INVALID_CHATROOM_INVITEE(HttpStatus.BAD_REQUEST, "E4004", "초대할 친구를 1명 이상 선택해야 합니다."),
    ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "E4005", "이미 신고한 항목입니다."),
    REPORT_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "E4006", "자기 자신은 신고할 수 없습니다."),
    EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, "E4007", "입력한 이메일이 일치하지 않습니다."),
    FILE_PREVIEW_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "E4008", "미리보기가 지원되지 않는 파일 형식입니다."),
    INVALID_TIME_VALUE(HttpStatus.BAD_REQUEST, "E4009", "목표 시간이 유효하지 않습니다."),
    ALREADY_STARTED(HttpStatus.BAD_REQUEST, "E4010", "이미 세션이 시작된 방입니다."),
    ALREADY_RAISED_HAND(HttpStatus.BAD_REQUEST, "E4011", "이미 손들기 요청을 보낸 사용자입니다."),
    INVALID_VOTE_SCORE(HttpStatus.BAD_REQUEST, "E4012", "유효하지 않은 점수입니다."),
    INVALID_ROOM_STATUS(HttpStatus.BAD_REQUEST, "E4013", "현재 방의 상태에서 해당 작업을 수행할 수 없습니다."),
    TARGET_TIME_NOT_REACHED(HttpStatus.BAD_REQUEST, "E4014", "아직 목표 공부 시간에 도달하지 않았습니다."),
    INVALID_POST_CATEGORY(HttpStatus.BAD_REQUEST, "E4015", "유효하지 않은 카테고리입니다."),
    STUDY_FULL(HttpStatus.BAD_REQUEST, "E4016", "스터디 정원이 이미 찼습니다."),

    // ❌ 중복 (409)
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "E4091", "이미 존재하는 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "E4092", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E4093", "이미 등록된 이메일입니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "E4094", "이미 신청한 스터디입니다."),
    ALREADY_PARTICIPATING(HttpStatus.CONFLICT, "E4095", "이미 참여 중인 스터디입니다."),
    DUPLICATE_LIKE(HttpStatus.BAD_REQUEST, "E4094", "이미 좋아요를 눌렀습니다."),
    DUPLICATE_REPORT(HttpStatus.BAD_REQUEST, "E4095", "이미 신고한 댓글입니다."),

    // 🔍 리소스 없음 (404)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4041", "해당 사용자를 찾을 수 없습니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4042", "해당 채팅방을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "E4043", "해당 게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E4044", "해당 댓글을 찾을 수 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "E4045", "해당 대댓글이 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "E4046", "해당 이메일을 찾을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4047", "첨부파일을 찾을 수 없습니다."),
    VIDEO_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4048", "해당 학습방을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4049", "해당 메시지를 찾을 수 없습니다."),
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "E4050", "해당 할 일을 찾을 수 없습니다."),
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "E4051", "스터디 정보를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "E4052", "신청 내역을 찾을 수 없습니다."),

    // 🛠 서버 내부 오류 (500)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E5001", "서버 내부 오류가 발생했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5002", "파일 삭제 중 오류가 발생했습니다."),
    FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5003", "파일 다운로드 중 오류가 발생했습니다."),
    FILE_PREVIEW_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5004", "파일 미리보기 생성 중 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5005", "이메일 전송에 실패했습니다."),

    // 📚 학습방
    TEAM_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E6001", "해당 학습방을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
