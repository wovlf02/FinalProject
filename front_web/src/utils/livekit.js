import {
    Room,
    VideoPresets,
    createLocalVideoTrack,
    createLocalAudioTrack,
} from 'livekit-client';

/**
 * ✅ 외장 캠 우선 사용 → 없으면 기본 카메라
 */
async function getPreferredVideoTrack() {
    const devices = await navigator.mediaDevices.enumerateDevices();
    const videoDevices = devices.filter((device) => device.kind === 'videoinput');

    if (videoDevices.length === 0) {
        throw new Error('사용 가능한 카메라 장치를 찾을 수 없습니다.');
    }

    const externalCam = videoDevices.find((d) => /HCAM01L/i.test(d.label));
    const selectedDeviceId = externalCam?.deviceId || videoDevices[0].deviceId;

    return createLocalVideoTrack({
        deviceId: selectedDeviceId,
        resolution: VideoPresets.h720.resolution,
    });
}

/**
 * ✅ LiveKit에 연결하고 트랙 publish
 *
 * @param {string} identity 사용자 고유 ID
 * @param {string} roomName 방 이름
 * @param {string} wsUrl WebSocket URL (ws:// 또는 wss://)
 * @param {string} token JWT 인증 토큰
 * @returns {Promise<Room>}
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

    // ✅ 디버깅용 이벤트
    room.on('participantConnected', (participant) => {
        console.log(`[LiveKit] 참가자 연결됨: ${participant.identity}`);
    });

    room.on('participantDisconnected', (participant) => {
        console.log(`[LiveKit] 참가자 퇴장: ${participant.identity}`);
    });

    room.on('disconnected', () => {
        console.log('[LiveKit] LiveKit 연결 종료됨');
    });

    try {
        await room.connect(wsUrl, token);
        console.log('[LiveKit] 서버 연결 성공');

        const [videoTrack, audioTrack] = await Promise.all([
            getPreferredVideoTrack(),
            createLocalAudioTrack(),
        ]);

        if (room.connected) {
            await room.localParticipant.publishTrack(videoTrack);
            await room.localParticipant.publishTrack(audioTrack);
            console.log('[LiveKit] 트랙 publish 완료');
        }

        // ✅ 트랙 publish한 후, React에서 video에 연결하는 부분은 외부에서 처리
        return room;
    } catch (err) {
        console.error('[LiveKit] 연결 실패 또는 트랙 publish 실패:', err);
        throw new Error(`LiveKit 연결 실패: ${err.name} - ${err.message}`);
    }
}
