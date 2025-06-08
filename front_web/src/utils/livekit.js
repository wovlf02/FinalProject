import {
    Room,
    VideoPresets,
    createLocalVideoTrack,
    createLocalAudioTrack,
} from 'livekit-client';

/**
 * ✅ "HCAM01L" 외장 카메라가 있으면 우선 사용, 없으면 fallback
 * 권한 미승인 상태면 getUserMedia 내부에서 거절됨
 */
async function getPreferredVideoTrack() {
    const devices = await navigator.mediaDevices.enumerateDevices();
    const videoDevices = devices.filter(device => device.kind === 'videoinput');

    if (videoDevices.length === 0) {
        throw new Error('사용 가능한 카메라 장치를 찾을 수 없습니다.');
    }

    const externalCam = videoDevices.find(d => /HCAM01L/i.test(d.label));
    const selectedDeviceId = externalCam?.deviceId || videoDevices[0].deviceId;

    return createLocalVideoTrack({
        deviceId: selectedDeviceId,
        resolution: VideoPresets.h720.resolution,
    });
}

/**
 * ✅ LiveKit 서버에 연결하고 트랙 publish 후 room 인스턴스 반환
 *
 * @param {string} identity - 사용자 ID
 * @param {string} roomName - LiveKit 방 이름
 * @param {string} wsUrl - WebSocket URL
 * @param {string} token - JWT 토큰
 * @param {string} [videoContainerId] - DOM ID (optional)
 * @returns {Promise<Room>}
 */
export async function connectToLiveKit(identity, roomName, wsUrl, token, videoContainerId) {
    const room = new Room({
        videoCaptureDefaults: {
            resolution: VideoPresets.h720.resolution,
        },
        publishDefaults: {
            simulcast: false,
        },
    });

    room.on('participantConnected', (participant) => {
        console.log(`[LiveKit] 참가자 연결됨: ${participant.identity}`);

        participant.on('trackSubscribed', (track) => {
            if (track.kind === 'video') {
                const id = `video-${participant.identity}`;
                let videoEl = document.getElementById(id);
                if (!videoEl) {
                    videoEl = document.createElement('video');
                    videoEl.id = id;
                    videoEl.autoplay = true;
                    videoEl.playsInline = true;
                    videoEl.className = 'remote-video';
                    const container = videoContainerId
                        ? document.getElementById(videoContainerId)
                        : document.body;
                    container?.appendChild(videoEl);
                }

                const mediaStream = new MediaStream([track.mediaStreamTrack]);
                videoEl.srcObject = mediaStream;
            }
        });
    });

    room.on('participantDisconnected', (participant) => {
        console.log(`[LiveKit] 참가자 퇴장: ${participant.identity}`);
        const el = document.getElementById(`video-${participant.identity}`);
        if (el) {
            el.srcObject = null;
            el.remove();
        }
    });

    room.on('disconnected', () => {
        console.log('[LiveKit] 연결 종료됨');
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
        }

        const localVideo = document.getElementById(`video-${identity}`);
        if (localVideo && !localVideo.srcObject) {
            const stream = new MediaStream([videoTrack.mediaStreamTrack]);
            localVideo.srcObject = stream;
        }

        return room;
    } catch (err) {
        console.error('[LiveKit] 연결 또는 트랙 publish 실패:', err);
        throw new Error(`LiveKit 연결 실패: ${err.name} - ${err.message}`);
    }
}
