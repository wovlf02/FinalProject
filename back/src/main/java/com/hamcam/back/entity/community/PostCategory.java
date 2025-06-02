package com.hamcam.back.entity.community;

import lombok.Getter;

import java.util.Arrays;

/**
 * 게시글 카테고리 Enum
 */
@Getter
public enum PostCategory {

    QUESTION("질문"),
    INFO("정보 공유"),
    STUDY("스터디"),
    ANONYMOUS("익명"),
    GENERAL("일반"),
    NOTICE("공지사항");

    private final String label;

    PostCategory(String label) {
        this.label = label;
    }

    /**
     * 한글 라벨로부터 Enum을 역매핑
     */
    public static PostCategory fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 라벨: " + label));
    }

    /**
     * Enum 이름(String)으로부터 대소문자 구분 없이 매핑
     */
    public static PostCategory fromNameIgnoreCase(String name) {
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 이름: " + name));
    }

    /**
     * 출력 시 라벨로 반환
     */
    @Override
    public String toString() {
        return label;
    }
}
