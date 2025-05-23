package com.hamcam.back.entity.community;

import lombok.Getter;

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
}
