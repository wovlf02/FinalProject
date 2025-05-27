package com.hamcam.back.util;

import java.util.List;
import java.util.Map;

/**
 * ✅ WebRTC ICE 서버(STUN/TURN) 정보 제공 유틸
 */
public class IceServerProvider {

    /**
     * ✅ 클라이언트에게 전달할 ICE 서버 리스트 반환
     * - STUN / TURN 서버 정보 포함
     */
    public static List<Map<String, Object>> getIceServers() {
        return List.of(
                Map.of(
                        "urls", "stun:stun.l.google.com:19302"
                ),
                Map.of(
                        "urls", "turn:openrelay.metered.ca:80",
                        "username", "openrelayproject",
                        "credential", "openrelayproject"
                )
                // 필요시 추가 TURN 서버도 여기에 등록 가능
        );
    }
}
