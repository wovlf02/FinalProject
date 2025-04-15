package com.hamcam.back.repository.auth;

import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA를 활용한 사용자 정보 접근 Layer
 * 기본적인 CRUD 기능 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 아이디로 사용자 조회 → 로그인 시 사용
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회 → 아이디 찾기 시 사용
     */
    Optional<User> findByEmail(String email);

    /**
     * Refresh Token으로 사용자 조회 → JWT Access Token 재발급 시 사용
     */
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 아이디 존재 여부 확인 → 아이디 중복 검사
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인 → 이메일 중복 검사
     */
    boolean existsByEmail(String email);

    /**
     * ✅ 닉네임 포함 사용자 목록 검색 → 친구 검색 기능
     */
    List<User> findByNicknameContaining(String nickname);
}
