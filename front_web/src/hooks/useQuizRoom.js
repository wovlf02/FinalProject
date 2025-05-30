// src/hooks/useQuizRoom.js
import { useState, useCallback } from 'react';

/**
 * QuizRoom 발표 흐름을 관리하는 Hook
 */
const useQuizRoom = () => {
    const [presenterId, setPresenterId] = useState(null);        // 현재 발표자 ID
    const [handRaisedList, setHandRaisedList] = useState([]);    // 손든 유저 ID 배열
    const [isVoting, setIsVoting] = useState(false);             // 투표 중 여부
    const [voteResult, setVoteResult] = useState(null);          // 투표 결과 (성공/실패/null)

    // 손들기
    const raiseHand = useCallback((userId) => {
        setHandRaisedList((prev) =>
            prev.includes(userId) ? prev : [...prev, userId]
        );
    }, []);

    const resetHandRaise = useCallback(() => {
        setHandRaisedList([]);
    }, []);

    // 발표자 설정
    const choosePresenter = useCallback((userId) => {
        setPresenterId(userId);
        setIsVoting(false);
        setVoteResult(null);
        resetHandRaise();
    }, [resetHandRaise]);

    // 투표 시작
    const startVote = useCallback(() => {
        setIsVoting(true);
        setVoteResult(null);
    }, []);

    // 투표 종료 후 결과 설정
    const finishVote = useCallback((isSuccess) => {
        setIsVoting(false);
        setVoteResult(isSuccess ? 'success' : 'fail');
    }, []);

    // 초기화
    const resetAll = useCallback(() => {
        setPresenterId(null);
        setHandRaisedList([]);
        setIsVoting(false);
        setVoteResult(null);
    }, []);

    return {
        presenterId,
        handRaisedList,
        isVoting,
        voteResult,
        raiseHand,
        choosePresenter,
        startVote,
        finishVote,
        resetHandRaise,
        resetAll
    };
};

export default useQuizRoom;
