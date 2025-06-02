import {
    Room,
    VideoPresets,
    createLocalVideoTrack,
    createLocalAudioTrack,
} from 'livekit-client';

/**
 * ✅ LiveKit 서버에 연결하고 트랙을 publish 한 후 room 인스턴스를 반환
 *
 * @param {string} identity - 사용자 ID (세션에서 가져온 고유 값)
 * @param {string} roomName - LiveKit 방 이름
 * @param {string} wsUrl - WebSocket 주소
 * @param {string} token - JWT 토큰
 * @param {string} [videoContainerId] - 비디오 엘리먼트를 삽입할 DOM ID (optional)
 * @returns {Promise<Room>} room 인스턴스
 */
export async function connectToLiveKit(identity, roomName, wsUrl, token, videoContainerId) {
    try {
        const room = new Room({
            videoCaptureDefaults: {
                resolution: VideoPresets.h720.resolution,
            },
            publishDefaults: {
                simulcast: false,
            },
        });

        // ✅ 참가자 연결 이벤트
        room.on('participantConnected', (participant) => {
            console.log(`[LiveKit] 참가자 연결됨: ${participant.identity}`);

            participant.on('trackSubscribed', (track, publication) => {
                if (track.kind === 'video') {
                    const id = `video-${participant.identity}`;
                    let videoEl = document.getElementById(id);
                    if (!videoEl) {
                        videoEl = document.createElement('video');
                        videoEl.id = id;
                        videoEl.autoplay = true;
                        videoEl.playsInline = true;
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

        // ✅ 참가자 퇴장 이벤트
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

        // ✅ LiveKit 서버 연결
        await room.connect(wsUrl, token);
        console.log('[LiveKit] 서버 연결 성공');

        // ✅ 로컬 비디오/오디오 트랙 생성 및 publish
        try {
            const [videoTrack, audioTrack] = await Promise.all([
                createLocalVideoTrack(),
                createLocalAudioTrack(),
            ]);

            if (room.connected) {
                if (videoTrack) await room.localParticipant.publishTrack(videoTrack);
                if (audioTrack) await room.localParticipant.publishTrack(audioTrack);
            }

            // ✅ 로컬 비디오 엘리먼트 DOM 설정
            const localVideo = document.getElementById(`video-${identity}`);
            if (localVideo && videoTrack && !localVideo.srcObject) {
                const stream = new MediaStream([videoTrack.mediaStreamTrack]);
                localVideo.srcObject = stream;
            }
        } catch (pubErr) {
            console.error('[LiveKit] 로컬 트랙 publish 중 오류:', pubErr);
        }

        return room;
    } catch (error) {
        console.error('[LiveKit] 연결 실패:', error.name, error.message, error.stack);
        throw new Error(`LiveKit 연결 실패: ${error.name} - ${error.message}`);
    }
}
