// src/hooks/useRoomLifecycle.js
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';

/**
 * 팀 학습방의 공통 입장/퇴장 흐름 관리 Hook
 *
 * @param {string} roomId - 현재 방 ID
 * @param {function} connectSocket - WebSocket 연결 함수
 * @param {function} disconnectSocket - WebSocket 종료 함수
 * @param {boolean} isHost - 방장 여부
 */
const useRoomLifecycle = ({ roomId, connectSocket, disconnectSocket, isHost = false }) => {
    const navigate = useNavigate();

    useEffect(() => {
        const handleJoin = async () => {
            try {
                await api.post('/video-room/join', { roomId });
                connectSocket(roomId); // WebSocket 연결
            } catch (error) {
                console.error('방 입장 실패:', error);
                alert('입장에 실패했습니다.');
                navigate('/team-study');
            }
        };

        handleJoin();

        // 페이지 이탈 또는 새로고침 시 퇴장 처리
        const handleBeforeUnload = async () => {
            try {
                await api.post('/video-room/leave', { roomId });
                disconnectSocket();
            } catch (err) {
                console.warn('퇴장 처리 실패:', err);
            }
        };

        window.addEventListener('beforeunload', handleBeforeUnload);

        return () => {
            handleBeforeUnload(); // unmount 시 퇴장
            window.removeEventListener('beforeunload', handleBeforeUnload);
        };
    }, [roomId, connectSocket, disconnectSocket, navigate]);

    /**
     * 방장이 방을 강제 종료하는 경우
     */
    const closeRoom = async () => {
        if (!isHost) return;

        try {
            await api.post('/video-room/close', { roomId });
            disconnectSocket();
            alert('방을 종료했습니다.');
            navigate('/team-study');
        } catch (error) {
            console.error('방 종료 실패:', error);
            alert('방 종료에 실패했습니다.');
        }
    };

    return { closeRoom };
};

export default useRoomLifecycle;
