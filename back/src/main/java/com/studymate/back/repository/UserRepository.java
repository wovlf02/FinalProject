package com.studymate.back.repository;

import com.studymate.back.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 정보를 관리하는 JPA Repository
 * JpaRepository<UserEntity, Integer>를 상속받아 기본적인 CRUD 기능 제공
 * 아이디, 이메일 등을 기준으로 사용자 조회
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    
    /**
     * 아이디로 사용자 정보 조회
     * @param username 아이디
     * @return Optional<UserEntity> 반환
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 이메일로 사용자 정보 조회
     * @param email 이메일
     * @return 회원가입 시 중복 이메일 검사를 위해 사용
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Refresh Token을 이용해 사용자 조회
     * @param refreshToken Refresh Token
     * @return JWT 기반 인증에서 Refresh Token 검증 시 사용
     */
    Optional<UserEntity> findByRefreshToken(String refreshToken);
}
