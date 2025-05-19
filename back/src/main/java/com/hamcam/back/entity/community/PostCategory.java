package com.hamcam.back.entity.community;

import lombok.Getter;

@Getter
public enum PostCategory {
    QUESTION("질문"),
    INFO("정보 공유"),
    STUDY("스터디"),
    ANONYMOUS("익명");

    private final String label;

    PostCategory(String label) {
        this.label = label;
    }

}
