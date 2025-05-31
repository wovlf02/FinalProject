import {
    Room,
    VideoPresets,
    createLocalVideoTrack,
    createLocalAudioTrack,
} from 'livekit-client';

/**
 * LiveKit 서버에 연결하고 트랙을 publish 한 후 room 인스턴스를 반환
 * @param {string} identity - 사용자 식별자
 * @param {string} roomName - 접속할 방 이름
 * @param {string} wsUrl - LiveKit WebSocket 주소
 * @param {string} token - JWT 토큰
 * @returns {Promise<Room>} - 연결된 room 인스턴스
 */
export async function connectToLiveKit(identity, roomName, wsUrl, token) {
    const room = new Room({
        videoCaptureDefaults: {
            resolution: VideoPresets.h720.resolution,
        },
        publishDefaults: {
            simulcast: false,
        },
    });

    // 이벤트 설정 (선택)
    room.on('participantConnected', (p) => {
        console.log(`${p.identity} 참가`);
    });
    room.on('participantDisconnected', (p) => {
        console.log(`${p.identity} 퇴장`);
    });

    await room.connect(wsUrl, token);

    const [videoTrack, audioTrack] = await Promise.all([
        createLocalVideoTrack(),
        createLocalAudioTrack()
    ]);
    await room.localParticipant.publishTrack(videoTrack);
    await room.localParticipant.publishTrack(audioTrack);

    return room;
}
