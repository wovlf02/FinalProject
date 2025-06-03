package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 랭킹 화면에서 “확인” 버튼 클릭 시 호출
 */
@Getter
@Setter
public class FocusConfirmExitRequest {
    private Long roomId;   // 종료할 방 ID
}
