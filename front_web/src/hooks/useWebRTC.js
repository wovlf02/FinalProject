// src/hooks/useWebRTC.js
import { useEffect, useRef, useState } from 'react';
import {
    Room,
    connect,
    createLocalVideoTrack,
    createLocalAudioTrack,
} from 'livekit-client';
import api from '../api/api';

const useWebRTC = (roomId) => {
    const [remoteStreams, setRemoteStreams] = useState([]);
    const [isCameraOn, setIsCameraOn] = useState(true);
    const [isMicOn, setIsMicOn] = useState(true);

    const localVideoRef = useRef(null);
    const roomRef = useRef(null);
    const videoTrackRef = useRef(null);
    const audioTrackRef = useRef(null);

    // ✅ 캠+마이크 실행 및 LiveKit 연결
    const startCamera = async () => {
        try {
            const res = await api.post('/livekit/token', { roomName: roomId });
            const { token, wsUrl } = res.data;

            const room = new Room();
            roomRef.current = room;

            // 원격 트랙 수신 시
            room.on('trackSubscribed', (track, publication, participant) => {
                if (track.kind === 'video') {
                    const stream = new MediaStream([track.mediaStreamTrack]);

                    setRemoteStreams((prev) => [
                        ...prev.filter((r) => r.id !== participant.identity),
                        {
                            id: participant.identity,
                            stream,
                            isPresenter: false,
                        },
                    ]);
                }
            });

            // ✅ connect는 Room 객체 메서드로 사용해야 함
            await room.connect(wsUrl, token);

            const videoTrack = await createLocalVideoTrack();
            const audioTrack = await createLocalAudioTrack();

            videoTrackRef.current = videoTrack;
            audioTrackRef.current = audioTrack;

            await room.localParticipant.publishTrack(videoTrack);
            await room.localParticipant.publishTrack(audioTrack);

            if (localVideoRef.current) {
                localVideoRef.current.srcObject = new MediaStream([videoTrack.mediaStreamTrack]);
            }
        } catch (err) {
            console.error('LiveKit 연결 실패:', err);
        }
    };


    // ✅ 캠 on/off
    const toggleCamera = () => {
        if (videoTrackRef.current) {
            const current = videoTrackRef.current.isEnabled;
            videoTrackRef.current.setEnabled(!current);
            setIsCameraOn(!current);
        }
    };

    // ✅ 마이크 on/off
    const toggleMic = () => {
        if (audioTrackRef.current) {
            const current = audioTrackRef.current.isEnabled;
            audioTrackRef.current.setEnabled(!current);
            setIsMicOn(!current);
        }
    };

    // ✅ 정리
    const stopMedia = () => {
        if (roomRef.current) {
            roomRef.current.disconnect();
            roomRef.current = null;
        }

        videoTrackRef.current?.stop();
        audioTrackRef.current?.stop();

        videoTrackRef.current = null;
        audioTrackRef.current = null;

        setRemoteStreams([]);
    };

    useEffect(() => {
        return () => {
            stopMedia();
        };
    }, []);

    return {
        localVideoRef,
        remoteStreams,
        startCamera,
        stopMedia,
        toggleCamera,
        toggleMic,
        isCameraOn,
        isMicOn,
    };
};

export default useWebRTC;

