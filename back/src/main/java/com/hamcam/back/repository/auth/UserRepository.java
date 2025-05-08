package com.hamcam.back.repository.auth;

import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 정보를 관리하는 JPA 리포지토리입니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자 아이디로 존재 여부 확인
     */
    boolean existsByUsername(String username);

    /**
     * 사용자 닉네임으로 존재 여부 확인
     */
    boolean existsByNickname(String nickname);

    /**
     * 이메일로 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 사용자 아이디로 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);
}
