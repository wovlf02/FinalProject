package com.studymate.back.repository;

import com.studymate.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA를 활용한 사용자 정보 접근 Layer
 * 기본적인 CRUD 기능 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 아이디로 사용자 조회 -> 로그인 시 사용
     * @param username 아이디
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회 -> 아이디 찾기 시 사용
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Refresh Token으로 사용자 조회 -> JWT Access Token 재발급 시 사용
     * @param refreshToken Refresh Token
     * @return Optional<User>
     */
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 아이디로 존재 여부 확인 -> 회원가입 시 아이디 중복 검사
     * @param username 아이디
     * @return true: 존재, false: 존재X
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인 -> 회원가입 시 이메일 중복 검사
     * @param email 이메일
     * @return true: 존재, false: 존재X
     */
    boolean existsByEmail(String email);
}
