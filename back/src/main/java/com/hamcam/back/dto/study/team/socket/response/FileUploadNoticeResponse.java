package com.hamcam.back.dto.study.team.socket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadNoticeResponse {

    private String uploaderNickname; // 업로더 닉네임
    private String fileName;         // 파일 이름
    private String downloadUrl;      // 다운로드 URL (또는 상대 경로)

}
