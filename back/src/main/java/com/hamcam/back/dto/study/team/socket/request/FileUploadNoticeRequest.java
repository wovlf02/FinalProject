package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileUploadNoticeRequest {

    private Long roomId;       // 업로드된 파일이 속한 방 ID
    private String fileName;   // 파일 이름
    private String fileUrl;    // 다운로드 가능한 URL (또는 상대 경로)

}
