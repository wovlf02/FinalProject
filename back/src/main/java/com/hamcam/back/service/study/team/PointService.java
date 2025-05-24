package com.hamcam.back.service.study.team;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import com.hamcam.back.global.exception.ErrorCode;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;

    /**
     * ✅ 포인트 지급 (세션 기반)
     */
    @Transactional
    public void grantPoint(HttpServletRequest request, int amount) {
        User user = getSessionUser(request);
        user.setPoint(user.getPoint() + amount);
    }

    /**
     * ✅ 현재 포인트 조회 (세션 기반)
     */
    @Transactional(readOnly = true)
    public int getCurrentPoint(HttpServletRequest request) {
        return getSessionUser(request).getPoint();
    }

    /**
     * ✅ 세션에서 사용자 조회
     */
    private User getSessionUser(HttpServletRequest request) {
        Long userId = SessionUtil.getUserId(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
