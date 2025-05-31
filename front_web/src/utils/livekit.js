import {
    Room,
    VideoPresets,
    createLocalVideoTrack,
    createLocalAudioTrack
} from 'livekit-client';

/**
 * LiveKit 서버에 연결하고 트랙을 publish 한 후 room 인스턴스를 반환
 *
 * @param {string} identity - 사용자 식별자
 * @param {string} roomName - 접속할 방 이름 (ex. "focus-1")
 * @param {string} wsUrl - LiveKit WebSocket 주소
 * @param {string} token - JWT 토큰
 * @returns {Promise<Room>} - 연결된 room 인스턴스
 */
export async function connectToLiveKit(identity, roomName, wsUrl, token) {
    try {
        // ✅ 1. 브라우저 권한 요청 (사용자가 거부하면 에러 발생)
        await navigator.mediaDevices.getUserMedia({ video: true, audio: true });

        // ✅ 2. Room 인스턴스 생성
        const room = new Room({
            videoCaptureDefaults: {
                resolution: VideoPresets.h720.resolution,
            },
            publishDefaults: {
                simulcast: false,
            },
        });

        // ✅ 3. 참가/퇴장 이벤트 등록
        room.on('participantConnected', (p) => {
            console.log(`[LiveKit] 참가자 연결됨: ${p.identity}`);
        });

        room.on('participantDisconnected', (p) => {
            console.log(`[LiveKit] 참가자 퇴장: ${p.identity}`);
        });

        // ✅ 4. LiveKit 서버 접속
        await room.connect(wsUrl, token);
        console.log('[LiveKit] 서버 연결 성공');

        // ✅ 5. 로컬 트랙 생성 및 publish
        const [videoTrack, audioTrack] = await Promise.all([
            createLocalVideoTrack(),
            createLocalAudioTrack()
        ]);

        await room.localParticipant.publishTrack(videoTrack);
        await room.localParticipant.publishTrack(audioTrack);

        return room;

    } catch (error) {
        console.error('[LiveKit] 연결 실패:', error.name, error.message);
        throw new Error(`LiveKit 연결 실패: ${error.name} - ${error.message}`);
    }
}
