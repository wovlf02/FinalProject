package com.hamcam.back.util;

public class RedisKeyUtil {

    public static String focusKey(Long roomId, Long userId) {
        return "focus:" + roomId + ":" + userId;
    }
}
