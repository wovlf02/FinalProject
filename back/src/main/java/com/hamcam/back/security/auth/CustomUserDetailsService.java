package com.hamcam.back.security.auth;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증 처리 서비스
 * - Spring Security에서 로그인 요청 시 호출됨
 * - username 기반으로 DB에서 사용자 조회
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 인증을 위해 호출하는 메서드
     *
     * @param username 로그인 시 입력한 사용자 ID
     * @return UserDetails 구현체 (CustomUserDetails)
     * @throws UsernameNotFoundException 해당 사용자가 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("해당 아이디로 등록된 사용자가 없습니다: " + username));

        // Security 인증 객체 생성
        return new CustomUserDetails(user);
    }
}
