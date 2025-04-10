package com.hamcam.back.common.util;

import java.util.regex.Pattern;

/**
 * [ValidationUtil]
 *
 * 입력 값 검증(Validation)을 위한 유틸리티 클래스
 *
 * [주요 기능]
 * - 이메일 형식 검사
 * - 닉네임 유효성 검사
 * - 사용자 ID 형식 검사
 * - 패스워드 복잡도 검사
 * - Null, 공백 체크
 */
public class ValidationUtil {

    // 이메일 정규식: 기본적인 email@domain 형식
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // 닉네임: 한글/영문/숫자, 2~12자
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,12}$");

    // 사용자 ID: 영문/숫자 조합, 4~20자
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,20}$");

    // 비밀번호: 영문 대/소문자, 숫자, 특수문자 중 3가지 이상 포함, 8~20자
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$");

    /**
     * 이메일 형식이 유효한지 검사
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 닉네임 형식이 유효한지 검사
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    /**
     * 사용자 ID 형식이 유효한지 검사
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 비밀번호 복잡도 조건을 만족하는지 검사
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 문자열이 null이거나 빈 문자열인지 검사
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 길이 제한 체크
     */
    public static boolean isLengthBetween(String value, int min, int max) {
        return value != null && value.length() >= min && value.length() <= max;
    }
}
