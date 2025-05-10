import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const PersonalStudy = () => {
    const [unitName, setUnitName] = useState('');
    const [minutes, setMinutes] = useState(0);
    const [seconds, setSeconds] = useState(0);
    const [showFocusMsg, setShowFocusMsg] = useState(false);
    const navigate = useNavigate();

    // 캠스터디 경고 및 집중 메시지
    const warningMsg = "⚠️ 캠에서 벗어나면 타이머가 멈춥니다. 화면을 벗어나지 말고 집중해 주세요!";
    const focusMsg = "집중해서 공부해보세요! 당신은 할 수 있습니다 💪";

    // 단원명과 시간(분/초) 모두 입력해야 버튼 활성화 (분+초 > 0)
    const isStartEnabled = unitName.trim() !== '' && (minutes > 0 || seconds > 0);

    const handleStart = () => {
        // 단원명, 공부시간(초)을 함께 전달
        navigate('/study', { state: { initialTime: minutes * 60 + seconds, unitName } });
    };

    // 집중 메시지 토글
    const handleFocusMsg = () => setShowFocusMsg(!showFocusMsg);

    // 초 입력은 0~59로 제한
    const handleSecondsChange = (e) => {
        let value = Number(e.target.value);
        if (value < 0) value = 0;
        if (value > 59) value = 59;
        setSeconds(value);
    };

    return (
        <div style={{ padding: '2rem', textAlign: 'center', maxWidth: 400, margin: '0 auto' }}>
            <h1>개인 공부 설정</h1>
            <div style={{ marginBottom: '1.5rem', color: '#d32f2f', fontWeight: 'bold', fontSize: '1.1rem' }}>
                {warningMsg}
            </div>
            <div style={{ marginBottom: '1rem' }}>
                <label>
                    <span style={{ display: 'block', marginBottom: '0.5rem' }}>단원명:</span>
                    <input
                        type="text"
                        value={unitName}
                        onChange={e => setUnitName(e.target.value)}
                        placeholder="예: 3단원 함수"
                        style={{ width: '90%', padding: '8px', fontSize: '1rem', borderRadius: '5px', border: '1px solid #ccc' }}
                    />
                </label>
            </div>
            <div style={{ marginBottom: '1.5rem' }}>
                <label>
                    <span style={{ display: 'block', marginBottom: '0.5rem' }}>학습 시간:</span>
                    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 8 }}>
                        <input
                            type="number"
                            min="0"
                            value={minutes}
                            onChange={e => setMinutes(Number(e.target.value))}
                            style={{ width: '60px', padding: '8px', fontSize: '1rem', borderRadius: '5px', border: '1px solid #ccc' }}
                        />
                        <span>분</span>
                        <input
                            type="number"
                            min="0"
                            max="59"
                            value={seconds}
                            onChange={handleSecondsChange}
                            style={{ width: '60px', padding: '8px', fontSize: '1rem', borderRadius: '5px', border: '1px solid #ccc' }}
                        />
                        <span>초</span>
                    </div>
                </label>
            </div>
            <button
                onClick={handleStart}
                disabled={!isStartEnabled}
                style={{
                    padding: '12px 28px',
                    background: isStartEnabled ? '#4CAF50' : '#aaa',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    fontSize: '1.1rem',
                    cursor: isStartEnabled ? 'pointer' : 'not-allowed',
                    marginBottom: '1.5rem'
                }}
            >
                공부 시작
            </button>
            <div>
                <button
                    onClick={handleFocusMsg}
                    style={{
                        background: '#1976d2',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        padding: '8px 16px',
                        fontSize: '0.95rem',
                        cursor: 'pointer'
                    }}
                >
                    {showFocusMsg ? '메시지 숨기기' : '집중 메시지 보기'}
                </button>
            </div>
            {showFocusMsg && (
                <div style={{ marginTop: '1rem', color: '#388e3c', fontWeight: 'bold' }}>
                    {focusMsg}
                </div>
            )}
        </div>
    );
};

export default PersonalStudy;
