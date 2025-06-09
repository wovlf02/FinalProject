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
    NO_PERMISSION(HttpStatus.FORBIDDEN, "E4033", "해당 작업에 대한 권한이 없습니다."),
    NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "E4034", "해당 방의 참가자가 아닙니다."),
    NOT_HOST(HttpStatus.FORBIDDEN, "E4035", "해당 방의 방장이 아닙니다."),
    USER_NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "E4036", "해당 방의 참가자가 아닙니다."),

    // ⚠️ 잘못된 요청 (400)
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E4001", "잘못된 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "E4002", "필수 파라미터가 누락되었습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E4003", "요청 정보가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "E4004", "비밀번호가 일치하지 않습니다."),
    ROOM_IS_FULL(HttpStatus.BAD_REQUEST, "E4005", "방 정원이 초과되었습니다."),
    ALREADY_JOINED_ROOM(HttpStatus.BAD_REQUEST, "E4006", "이미 해당 방에 참여 중입니다."),
    ALREADY_STARTED(HttpStatus.BAD_REQUEST, "E4007", "이미 시작된 방입니다."),
    INVALID_POST_CATEGORY(HttpStatus.BAD_REQUEST, "E4008", "적절하지 않은 카테고리입니다."),
    STUDY_FULL(HttpStatus.BAD_REQUEST, "E4009", "스터디 인원이 가득 찼습니다."),
    REPORT_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "E4010", "자신을 신고할 수 없습니다."),
    FILE_PREVIEW_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "E4011", "해당 파일 형식은 미리보기를 지원하지 않습니다."),
    INVALID_CHATROOM_INVITEE(HttpStatus.BAD_REQUEST, "E4012", "채팅방에 초대할 수 없는 사용자입니다."),
    MINIMUM_PARTICIPANT_NOT_MET(HttpStatus.BAD_REQUEST, "E4013", "문제풀이를 시작하려면 최소 3명이 필요합니다."),
    ALREADY_JOINED(HttpStatus.BAD_REQUEST, "E4014", "이미 방에 참여한 상태입니다."),
    INVALID_ROOM_TYPE(HttpStatus.BAD_REQUEST, "E4015", "유효하지 않은 방 타입입니다."),
    INVALID_OPERATION(HttpStatus.BAD_REQUEST, "E4016", "허용되지 않는 작업입니다."),


    // ❌ 중복 (409)
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "E4091", "이미 존재하는 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "E4092", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E4093", "이미 등록된 이메일입니다."),
    DUPLICATE_LIKE(HttpStatus.BAD_REQUEST, "E4094", "이미 좋아요를 눌렀습니다."),
    DUPLICATE_REPORT(HttpStatus.BAD_REQUEST, "E4095", "이미 신고한 댓글입니다."),
    DUPLICATE_VOTE(HttpStatus.CONFLICT, "E4096", "이미 투표가 완료되었습니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "E4097", "이미 신청한 상태입니다."),
    ALREADY_PARTICIPATING(HttpStatus.CONFLICT, "E4098", "이미 스터디에 참여 중입니다."),
    ALREADY_REPORTED(HttpStatus.CONFLICT, "E4099", "이미 신고한 대상입니다."),

    // 🔍 리소스 없음 (404)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4041", "해당 사용자를 찾을 수 없습니다."),
    VIDEO_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4042", "해당 학습방을 찾을 수 없습니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4043", "해당 방을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "E4044", "해당 게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E4045", "해당 댓글을 찾을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4046", "해당 파일을 찾을 수 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "E4047", "해당 대댓글을 찾을 수 없습니다."),
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "E4048", "해당 스터디를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "E4049", "신청 정보를 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4050", "해당 메시지를 찾을 수 없습니다."),
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "E4051", "해당 TODO 항목을 찾을 수 없습니다."),
    STUDY_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4052", "해당 학습방을 찾을 수 없습니다."),
    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4053", "해당 문제를 찾을 수 없습니다."),

    // 🛠 서버 내부 오류 (500)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E5001", "서버 내부 오류가 발생했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5002", "파일 삭제 중 오류가 발생했습니다."),
    FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5003", "파일 다운로드 중 오류가 발생했습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5004", "파일 업로드에 실패했습니다."),
    FILE_PREVIEW_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5005", "파일 미리보기 생성 중 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5006", "이메일 전송 중 오류가 발생했습니다."),

    // 📚 WebRTC 팀 학습방 전용
    ROOM_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "E6001", "이미 종료된 방입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
