package com.hamcam.back.auth.service;

import com.hamcam.back.auth.entity.User;
import com.hamcam.back.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * [Spring Security 사용자 인증용 서비스]
 *
 * Spring Security가 로그인 시 호출하는 서비스
 * 입력된 아이디(username)를 기준으로 DB에서 사용자 정보를 조회하고
 * 인증 처리를 위한 UserDetails 형태로 반환한다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 정보 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // User 엔티티는 Spring Security의 UserDetails를 구현해야 함
        return user;
    }
}
