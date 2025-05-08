package com.hamcam.back.dto.video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoRoomRequest {
    private Long teamId;
    private String title;
}
