import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/api';
import '../css/FocusRoom.css';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const FocusRoom = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();

    const [focusedSeconds, setFocusedSeconds] = useState(0);
    const [ranking, setRanking] = useState([]);
    const [winnerId, setWinnerId] = useState(null);
    const [confirmed, setConfirmed] = useState(false);

    const stompRef = useRef(null);
    const intervalRef = useRef(null);

    const roomName = `focus-${roomId}`;

    useEffect(() => {
        enterRoom();
        connectLiveKit();
        connectWebSocket();

        return () => {
            if (intervalRef.current) clearInterval(intervalRef.current);
            if (stompRef.current) stompRef.current.disconnect();
        };
    }, []);

    /** ✅ 서버에 참가자 등록 (세션 기반) */
    const enterRoom = async () => {
        try {
            await api.post('/study/team/enter', null, { params: { roomId } });
        } catch (error) {
            alert('입장 실패: 로그인 필요 또는 존재하지 않는 방');
            navigate('/study/team');
        }
    };

    /** ✅ LiveKit 토큰 요청 후 캠 연결 */
    const connectLiveKit = async () => {
        try {
            // 1. 세션 기반 사용자 정보 요청
            const userRes = await api.get('/users/me');
            const identity = userRes.data?.user_id; // 또는 nickname도 가능
            console.log(userRes);

            // 2. LiveKit 토큰 요청
            const res = await api.post('/livekit/token', {
                identity,
                roomName
            });

            const token = res.data.token;

            // 3. 미디어 스트리밍 시작 (로컬 대체)
            const stream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true
            });
            const videoElement = document.getElementById('video');
            if (videoElement) {
                videoElement.srcObject = stream;
            }

        } catch (e) {
            console.error('LiveKit 연결 실패:', e);
            alert('LiveKit 연결 실패: 로그인 필요 또는 서버 오류');
        }
    };


    /** ✅ WebSocket 연결 및 실시간 구독 */
    const connectWebSocket = () => {
        const sock = new SockJS('/ws');
        const client = Stomp.over(sock);
        stompRef.current = client;

        client.connect({}, () => {
            client.subscribe(`/sub/focus/room/${roomId}`, (message) => {
                const body = message.body;
                if (body === 'TERMINATED') {
                    alert('방이 종료되었습니다.');
                    navigate('/study/team');
                    return;
                }

                const parsed = JSON.parse(body);
                setRanking(parsed.ranking || []);
            });

            client.subscribe(`/sub/focus/room/${roomId}/winner`, (message) => {
                setWinnerId(Number(message.body));
            });

            // ✅ 1분마다 집중 시간 서버로 전송
            intervalRef.current = setInterval(() => {
                const payload = {
                    roomId: Number(roomId),
                    focusedSeconds: 60,
                };
                stompRef.current.send('/app/focus/update-time', {}, JSON.stringify(payload));
                setFocusedSeconds(prev => prev + 60);
            }, 60000);
        });
    };

    /** ✅ 목표 시간 도달 시 알림 */
    const handleGoal = () => {
        stompRef.current.send('/app/focus/goal-achieved', {}, JSON.stringify({
            room_id: Number(roomId)
        }));
    };

    /** ✅ 결과 확인 클릭 */
    const handleConfirmExit = () => {
        setConfirmed(true);
        stompRef.current.send('/app/focus/confirm-exit', {}, JSON.stringify({
            room_id: Number(roomId)
        }));
    };

    return (
        <div className="focus-room-container">
            <h1>📚 공부 집중방</h1>

            <div className="video-section">
                <video id="video" autoPlay muted playsInline />
            </div>

            <div className="info-section">
                <h2>🕒 집중 시간: {Math.floor(focusedSeconds / 60)}분 {focusedSeconds % 60}초</h2>

                {winnerId && (
                    <p className="winner">🎉 승리자: 사용자 {winnerId}번!</p>
                )}

                <div className="button-group">
                    <button onClick={handleGoal}>🎯 목표 달성</button>
                    <button onClick={handleConfirmExit} disabled={confirmed}>✅ 결과 확인</button>
                </div>

                <h3>📊 실시간 랭킹</h3>
                <ul className="ranking-list">
                    {ranking.length === 0 ? (
                        <p>랭킹 정보를 불러오는 중...</p>
                    ) : (
                        ranking.map((user, index) => (
                            <li key={user.userId}>
                                {index + 1}. {user.nickname} - {user.focusedSeconds}초
                            </li>
                        ))
                    )}
                </ul>
            </div>
        </div>
    );
};

export default FocusRoom;
