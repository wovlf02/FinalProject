package com.hamcam.back.util;

public class InviteCodeGenerator {
    public static String generate() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}
