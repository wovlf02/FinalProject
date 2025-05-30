// src/hooks/useFocusTimer.js
import { useEffect, useRef, useState } from 'react';
import api from '../api/api';

/**
 * Focus Study Room 집중 타이머 Hook
 * @param {number} roomId - 현재 방 ID
 * @param {number} targetTime - 목표 시간 (분 단위)
 * @param {function} onComplete - 목표 도달 시 콜백
 */
const useFocusTimer = ({ roomId, targetTime, onComplete }) => {
    const [seconds, setSeconds] = useState(0);
    const [isRunning, setIsRunning] = useState(true);
    const timerRef = useRef(null);
    const syncRef = useRef(null); // 1분마다 서버 전송용

    // 1초 간격 집중 시간 측정
    useEffect(() => {
        if (isRunning) {
            timerRef.current = setInterval(() => {
                setSeconds(prev => prev + 1);
            }, 1000);
        }
        return () => clearInterval(timerRef.current);
    }, [isRunning]);

    // 60초마다 서버에 집중 시간 전송
    useEffect(() => {
        if (!roomId || seconds === 0) return;

        if (seconds % 60 === 0) {
            syncRef.current = setTimeout(() => {
                api.post('/study-room/focus-time', {
                    roomId,
                    focusTime: seconds,
                }).catch((err) => {
                    console.error('집중 시간 동기화 실패:', err);
                });
            }, 0);
        }

        if (targetTime && seconds >= targetTime * 60) {
            setIsRunning(false);
            onComplete?.(); // 목표 도달 시 콜백
        }

        return () => clearTimeout(syncRef.current);
    }, [seconds, roomId, targetTime, onComplete]);

    const resetTimer = () => {
        setSeconds(0);
        setIsRunning(true);
    };

    return {
        seconds,
        isRunning,
        setIsRunning,
        resetTimer,
    };
};

export default useFocusTimer;
