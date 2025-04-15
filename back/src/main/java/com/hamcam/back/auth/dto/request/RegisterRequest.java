package com.hamcam.back.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * [회원가입 요청 DTO]
 *
 * 사용자가 회원가입 폼에서 입력한 정보들을 담는 요청 객체
 * 프론트에서는 multipart/form-data 형식으로 요청을 보내며,
 * 텍스트 정보와 함께 이미지 파일도 포함될 수 있음
 *
 * [사용 API]
 * POST /api/auth/register
 *
 * [전송 방식]
 * @ModelAttribute 사용 (JSON이 아닌 Multipart 요청)
 * 이미지 파일 업로드와 텍스트를 함께 처리 가능
 *
 * [유효성 검사는 서비스 계층에서 별도로 수행]
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    /**
     * 사용자 ID (로그인에 사용)
     * 고유값
     * 아이디 중복 체크 API로 사전 검증 필요
     */
    private String username;

    /**
     * 사용자 비밀번호
     * 프론트에서는 마스킹 처리
     * 서버에서 BCrypt 해시로 암호화
     */
    private String password;

    /**
     * 사용자 실명 또는 이름
     * 본인 확인용 필드
     */
    private String name;

    /**
     * 사용자 닉네임
     * 커뮤니티 활동 시 표시되는 별명
     * 중복 불가, unique constraint 필요
     * 닉네임 중복 확인 API를 통해 사진 확인
     */
    private String nickname;

    /**
     * 사용자 전화번호
     * 형식 예시: 010-1234-5678
     */
    private String phone;

    /**
     * 사용자 이메일 주소
     * 아이디/비밀번호 찾기 및 본인 인증에 사용
     * 이메일 인증 API에서 인증이 완료된 값이어야 함
     */
    private String email;

    /**
     * 사용자 학습 목표
     * ex) 하루 3시간 이상 공부하기, 2027 수능 대비
     * 마이페이지 등에서 사용자 동기 부여에 활용 가능
     */
    private String goal;

    /**
     * 사용자 프로필 이미지 (선택)
     * 이미지 파일 자체 (JPEG, PNG 등)
     * 서버에서 바이너리 데이터 처리 또는 S3 등 외부 저장소 업로드 가능
     * 파일 용량 및 타입 제한 필요
     */
    private MultipartFile profileImage;
}
