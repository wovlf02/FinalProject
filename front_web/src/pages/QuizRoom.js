import React, { useEffect, useRef, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/api';
import '../css/QuizRoom.css';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const QuizRoom = () => {
    const { roomId } = useParams();
    const navigate = useNavigate();

    const [problem, setProblem] = useState(null);
    const [presenterId, setPresenterId] = useState(null);
    const [votePhase, setVotePhase] = useState(false);
    const [voteResult, setVoteResult] = useState(null);
    const [userId, setUserId] = useState(null);

    const stompRef = useRef(null);
    console.log(roomId);

    useEffect(() => {
        enterRoom();
        fetchUserInfo(); // ✅ userId 먼저 가져오기
        connectWebSocket();

        return () => {
            if (stompRef.current) stompRef.current.disconnect();
        };
    }, []);

    /** ✅ 세션 기반 입장 등록 */
    const enterRoom = async () => {
        try {
            await api.post('/study/team/enter', null, { params: { roomId } });
        } catch (err) {
            alert('방 입장 실패');
            navigate('/study/team');
        }
    };

    /** ✅ 사용자 ID 조회 후 LiveKit 연결 */
    const fetchUserInfo = async () => {
        try {
            const res = await api.get('/users/me');
            setUserId(res.data.user_id);
            connectLiveKit(res.data.user_id); // ✅ userId 전달
        } catch (err) {
            console.error('유저 정보 조회 실패', err);
        }
    };

    /** ✅ LiveKit 연결 */
    const connectLiveKit = async (identity) => {
        try {
            const res = await api.post('/livekit/token', {
                identity,
                room_name: `quiz-${roomId}`,
            });
            const token = res.data.token;

            // ✅ 캠/마이크 연결
            const stream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true,
            });
            const videoElement = document.getElementById('video');
            if (videoElement) {
                videoElement.srcObject = stream;
            }
        } catch (err) {
            console.error('LiveKit 연결 실패', err);
            alert('LiveKit 연결 실패');
        }
    };

    /** ✅ WebSocket 연결 및 흐름 수신 */
    const connectWebSocket = () => {
        const sock = new SockJS('/ws');
        const client = Stomp.over(sock);
        stompRef.current = client;

        client.connect({}, () => {
            client.send('/app/quiz/enter', {}, JSON.stringify({ roomId }));

            client.subscribe(`/sub/quiz/room/${roomId}/problem`, (msg) => {
                const data = JSON.parse(msg.body);
                setProblem(data);
                setPresenterId(null);
                setVotePhase(false);
                setVoteResult(null);
            });

            client.subscribe(`/sub/quiz/room/${roomId}/presenter`, (msg) => {
                const id = Number(msg.body);
                setPresenterId(id);
            });

            client.subscribe(`/sub/quiz/room/${roomId}/vote-result`, (msg) => {
                const result = JSON.parse(msg.body);
                setVoteResult(result);
                setVotePhase(false);
            });
        });
    };

    const handleStart = () => {
        stompRef.current.send('/app/quiz/start', {}, JSON.stringify({ roomId }));
    };

    const handleRaiseHand = () => {
        stompRef.current.send('/app/quiz/hand', {}, JSON.stringify({ roomId }));
    };

    const handleEndPresentation = () => {
        stompRef.current.send('/app/quiz/end-presentation', {}, JSON.stringify({ roomId }));
        setVotePhase(true);
    };

    const handleVote = (isSuccess) => {
        stompRef.current.send('/app/quiz/vote', {}, JSON.stringify({
            roomId,
            vote: isSuccess ? 'SUCCESS' : 'FAIL',
        }));
    };

    const handleContinue = () => {
        stompRef.current.send('/app/quiz/continue', {}, JSON.stringify({ roomId }));
        setProblem(null);
        setVoteResult(null);
    };

    const handleTerminate = () => {
        stompRef.current.send('/app/quiz/terminate', {}, JSON.stringify({ roomId }));
        navigate('/study/team');
    };

    return (
        <div className="quiz-room-container">
            <h1>🧠 문제풀이방</h1>

            <div className="video-section">
                <video id="video" autoPlay muted playsInline />
            </div>

            {problem ? (
                <div className="problem-section">
                    <h2>{problem.title}</h2>
                    <p className="passage">{problem.passage}</p>
                    <ul>
                        {problem.choices.map((c, idx) => (
                            <li key={idx}>{String.fromCharCode(65 + idx)}. {c}</li>
                        ))}
                    </ul>
                </div>
            ) : (
                <p>문제를 시작해주세요.</p>
            )}

            {presenterId && (
                <div className="presenter-section">
                    <p>🗣️ 발표자: 사용자 {presenterId}</p>
                    {presenterId === userId && (
                        <button onClick={handleEndPresentation}>🎤 발표 종료</button>
                    )}
                </div>
            )}

            {votePhase && (
                <div className="vote-section">
                    <h3>발표는 어땠나요?</h3>
                    <button onClick={() => handleVote(true)}>👍 성공</button>
                    <button onClick={() => handleVote(false)}>👎 실패</button>
                </div>
            )}

            {voteResult && (
                <div className="result-section">
                    <h3>🗳️ 투표 결과</h3>
                    <p>성공: {voteResult.successCount}명</p>
                    <p>실패: {voteResult.failCount}명</p>
                    <p>결과: <strong>{voteResult.result === 'SUCCESS' ? '정답 인정!' : '정답 미인정'}</strong></p>
                </div>
            )}

            <div className="action-buttons">
                {!problem && <button onClick={handleStart}>문제 시작</button>}
                {problem && !presenterId && <button onClick={handleRaiseHand}>✋ 손들기</button>}
                {voteResult && (
                    <>
                        <button onClick={handleContinue}>🔁 계속하기</button>
                        <button onClick={handleTerminate}>⛔ 종료하기</button>
                    </>
                )}
            </div>
        </div>
    );
};

export default QuizRoom;
