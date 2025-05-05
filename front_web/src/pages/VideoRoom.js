import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { io } from 'socket.io-client'; // Socket.IO 클라이언트 추가

const VideoRoom = () => {
    const { roomId } = useParams(); // URL에서 roomId 가져오기
    const navigate = useNavigate(); // 페이지 이동을 위한 navigate 추가
    const localVideoRef = useRef(null);
    const peerConnections = useRef({}); // 여러 PeerConnection을 관리하기 위한 객체
    const socket = useRef(null);
    const localStreamRef = useRef(null); // localStream을 전역적으로 관리
    const [remoteStreams, setRemoteStreams] = useState([]); // 원격 스트림 관리
    const [userCount, setUserCount] = useState(0); // 사용자 수 상태 추가

    useEffect(() => {
        // WebSocket 연결 설정
        socket.current = io('http://localhost:8081'); // Signaling 서버 URL
        const userId = `${roomId}-${Math.random().toString(36).substr(2, 9)}`; // 고유 사용자 ID 생성
        socket.current.emit('join-room', { roomId, userId }); // 사용자 ID 포함

        // WebRTC 초기화 로직
        const startWebRTC = async () => {
            try {
                if (!localStreamRef.current) {
                    const devices = await navigator.mediaDevices.enumerateDevices();
                    const videoInputDevices = devices.filter(device => device.kind === 'videoinput');

                    if (videoInputDevices.length === 0) {
                        console.warn('사용 가능한 카메라가 없습니다.');
                        return;
                    }

                    const localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
                    localStreamRef.current = localStream; // localStream 저장

                    if (localVideoRef.current) {
                        localVideoRef.current.srcObject = localStream;
                    } else {
                        console.warn('localVideoRef is not available.');
                    }
                }

                // 새로운 사용자 연결 처리
                socket.current.on('user-connected', (newUserId) => {
                    console.log('User connected:', newUserId); // 디버깅 로그 추가
                    const peerConnection = new RTCPeerConnection();
                    peerConnections.current[newUserId] = peerConnection;

                    // Local stream 추가
                    localStreamRef.current.getTracks().forEach((track) => peerConnection.addTrack(track, localStreamRef.current));

                    // Remote stream 설정
                    peerConnection.ontrack = (event) => {
                        setRemoteStreams((prevStreams) => {
                            const newStream = event.streams[0];
                            if (!prevStreams.find((stream) => stream.id === newStream.id)) {
                                return [...prevStreams, newStream];
                            }
                            return prevStreams;
                        });
                    };

                    // ICE Candidate 처리
                    peerConnection.onicecandidate = (event) => {
                        if (event.candidate) {
                            socket.current.emit('ice-candidate', { roomId, userId: newUserId, candidate: event.candidate });
                        }
                    };

                    // Offer 생성 및 전송
                    peerConnection.createOffer().then((offer) => {
                        peerConnection.setLocalDescription(offer);
                        socket.current.emit('offer', { roomId, userId: newUserId, offer });
                    });
                });

                // Offer/Answer 처리
                socket.current.on('offer', async ({ userId, offer }) => {
                    const peerConnection = new RTCPeerConnection();
                    peerConnections.current[userId] = peerConnection;

                    // Local stream 추가
                    localStreamRef.current.getTracks().forEach((track) => peerConnection.addTrack(track, localStreamRef.current));

                    // Remote stream 설정
                    peerConnection.ontrack = (event) => {
                        setRemoteStreams((prevStreams) => {
                            const newStream = event.streams[0];
                            if (!prevStreams.find((stream) => stream.id === newStream.id)) {
                                return [...prevStreams, newStream];
                            }
                            return prevStreams;
                        });
                    };

                    // ICE Candidate 처리
                    peerConnection.onicecandidate = (event) => {
                        if (event.candidate) {
                            socket.current.emit('ice-candidate', { roomId, userId, candidate: event.candidate });
                        }
                    };

                    await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
                    const answer = await peerConnection.createAnswer();
                    await peerConnection.setLocalDescription(answer);
                    socket.current.emit('answer', { roomId, userId, answer });
                });

                socket.current.on('answer', async ({ userId, answer }) => {
                    const peerConnection = peerConnections.current[userId];
                    if (peerConnection) {
                        await peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
                    }
                });

                socket.current.on('ice-candidate', async ({ userId, candidate }) => {
                    const peerConnection = peerConnections.current[userId];
                    if (peerConnection) {
                        try {
                            await peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
                        } catch (error) {
                            console.error('Error adding received ICE candidate', error);
                        }
                    }
                });

                // 사용자 연결 해제 처리
                socket.current.on('user-disconnected', (disconnectedUserId) => {
                    console.log('User disconnected:', disconnectedUserId); // 디버깅 로그 추가
                    if (peerConnections.current[disconnectedUserId]) {
                        peerConnections.current[disconnectedUserId].close();
                        delete peerConnections.current[disconnectedUserId];
                    }
                    setRemoteStreams((prevStreams) =>
                        prevStreams.filter((stream) => stream.id !== disconnectedUserId)
                    );
                });

                socket.current.on('update-user-count', (count) => {
                    setUserCount(count); // 사용자 수 업데이트
                });
            } catch (error) {
                console.error('Error initializing WebRTC:', error);
            }
        };

        startWebRTC();

        return () => {
            // 안전하게 WebSocket 및 PeerConnection 종료
            if (socket.current) {
                socket.current.disconnect();
                socket.current = null;
            }
            Object.values(peerConnections.current).forEach((peerConnection) => peerConnection.close());
            peerConnections.current = {};
            if (localStreamRef.current) {
                localStreamRef.current.getTracks().forEach(track => track.stop());
                localStreamRef.current = null;
            }
        };
    }, [roomId]);

    useEffect(() => {
        // 최대 9명을 초과하면 페이지 이동
        if (remoteStreams.length >= 9) {
            navigate('/room-full'); // 방이 가득 찼을 때 이동할 페이지
        }
    }, [remoteStreams, navigate]);

    const addLocalStream = async () => {
        try {
            const additionalStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
            setRemoteStreams((prevStreams) => [...prevStreams, additionalStream]);
        } catch (error) {
            console.error('추가 스트림 생성 중 오류:', error);
        }
    };

    return (
        <div>
            <h1>화상 채팅 방 - {roomId}</h1>
            <p>현재 사용자 수: {userCount}명</p> {/* 사용자 수 표시 */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '10px' }}>
                <video ref={localVideoRef} autoPlay muted style={{ width: '100%' }} />
                {remoteStreams.map((stream, index) => (
                    <video
                        key={index}
                        autoPlay
                        style={{ width: '100%' }}
                        ref={(video) => {
                            if (video) video.srcObject = stream;
                        }}
                    />
                ))}
            </div>
            <button onClick={addLocalStream}>추가 카메라 테스트</button>
        </div>
    );
};

export default VideoRoom;
