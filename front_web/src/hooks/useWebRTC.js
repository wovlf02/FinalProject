// src/hooks/useWebRTC.js
import { useEffect, useRef, useState } from 'react';

const useWebRTC = () => {
    const [localStream, setLocalStream] = useState(null);
    const [isCameraOn, setIsCameraOn] = useState(true);
    const [isMicOn, setIsMicOn] = useState(true);
    const videoRef = useRef(null);

    // ✅ 캠/마이크 스트림 요청
    const startMedia = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true,
            });

            setLocalStream(stream);

            // 자동 연결
            if (videoRef.current) {
                videoRef.current.srcObject = stream;
            }
        } catch (err) {
            console.error('미디어 스트림 접근 실패:', err);
        }
    };

    // ✅ 캠 off/on
    const toggleCamera = () => {
        if (localStream) {
            localStream.getVideoTracks().forEach((track) => {
                track.enabled = !track.enabled;
            });
            setIsCameraOn((prev) => !prev);
        }
    };

    // ✅ 마이크 off/on
    const toggleMic = () => {
        if (localStream) {
            localStream.getAudioTracks().forEach((track) => {
                track.enabled = !track.enabled;
            });
            setIsMicOn((prev) => !prev);
        }
    };

    // ✅ 정리: 캠 종료
    const stopMedia = () => {
        if (localStream) {
            localStream.getTracks().forEach((track) => track.stop());
            setLocalStream(null);
        }
    };

    // ✅ 초기 진입 시 자동 스트림 요청
    useEffect(() => {
        startMedia();

        return () => {
            stopMedia();
        };
    }, []);

    return {
        localStream,
        videoRef,
        startMedia,
        stopMedia,
        toggleCamera,
        toggleMic,
        isCameraOn,
        isMicOn,
    };
};

export default useWebRTC;
