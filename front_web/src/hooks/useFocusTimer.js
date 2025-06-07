// src/hooks/useFocusTimer.js
import { useEffect, useRef, useState } from 'react';
import useTeamRoomSocket from './useTeamRoomSocket';

/**
 * Focus Study Room 집중 타이머 Hook (WebSocket 연동)
 *
 * @param {number} roomId - 현재 방 ID
 * @param {number} targetTime - 목표 시간 (분 단위)
 * @param {function} onComplete - 목표 도달 시 콜백
 */
const useFocusTimer = ({ roomId, targetTime, onComplete }) => {
    const [seconds, setSeconds] = useState(0);
    const [isRunning, setIsRunning] = useState(true);
    const timerRef = useRef(null);

    // ✅ WebSocket 전송용 Hook
    const {
        connectSocket,
        disconnectSocket,
        sendEvent,
    } = useTeamRoomSocket(
        roomId,
        null,
        (event) => {
            if (event.type === 'RANK_UPDATE') {
                console.log('📊 실시간 랭킹:', event.ranking);
            }
        }
    );

    // ✅ 타이머 1초 간격 증가
    useEffect(() => {
        if (isRunning) {
            timerRef.current = setInterval(() => {
                setSeconds((prev) => prev + 1);
            }, 1000);
        }

        return () => clearInterval(timerRef.current);
    }, [isRunning]);

    // ✅ 1분마다 서버에 집중 시간 전송
    useEffect(() => {
        if (!roomId || seconds === 0) return;

        if (seconds % 60 === 0) {
            sendEvent('FOCUS_TIME_UPDATE', {
                focusedSeconds: seconds
            });
        }

        // ✅ 목표 도달
        if (targetTime && seconds >= targetTime * 60) {
            setIsRunning(false);
            sendEvent('FOCUS_GOAL_ACHIEVED');
            onComplete?.();
        }
    }, [seconds]);

    const resetTimer = () => {
        setSeconds(0);
        setIsRunning(true);
    };

    return {
        seconds,
        isRunning,
        setIsRunning,
        resetTimer,
        connectSocket,
        disconnectSocket,
    };
};

export default useFocusTimer;

