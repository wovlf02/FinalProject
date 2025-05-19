import React, { useState } from 'react';

const DashboardDday = () => {
    const [examName, setExamName] = useState(() => localStorage.getItem('examName') || '');
    const [examDate, setExamDate] = useState(() => {
        const saved = localStorage.getItem('examDate');
        return saved ? new Date(saved) : new Date(Date.now() + 1000 * 60 * 60 * 24 * 20); // 기본 20일 후
    });
    const [showExamSetting, setShowExamSetting] = useState(false);
    const [tempExamName, setTempExamName] = useState('');
    const [tempExamDate, setTempExamDate] = useState('');

    const calculateDday = () => {
        if (!examDate) return 0;
        const today = new Date();
        const target = new Date(examDate);
        today.setHours(0, 0, 0, 0);
        target.setHours(0, 0, 0, 0);
        return Math.floor((target - today) / (1000 * 60 * 60 * 24));
    };

    const dDay = calculateDday();

    const openExamSetting = () => {
        setTempExamName(examName);
        setTempExamDate(examDate ? examDate.toISOString().split('T')[0] : '');
        setShowExamSetting(true);
    };

    const saveExamSetting = () => {
        const parsedDate = new Date(tempExamDate);
        setExamName(tempExamName);
        setExamDate(parsedDate);
        localStorage.setItem('examName', tempExamName);
        localStorage.setItem('examDate', parsedDate.toISOString());
        setShowExamSetting(false);
    };

    return (
        <div className="dashboard-dday">
            {examName ? (
                <>
                    <span style={{ marginRight: 8 }}>{examName}</span>
                    <span>
            {dDay > 0 ? `D-${dDay}` : dDay === 0 ? 'D-DAY' : `D+${Math.abs(dDay)}`}
          </span>
                </>
            ) : (
                <span>시험일을 설정해주세요</span>
            )}
            <button
                onClick={openExamSetting}
                style={{
                    background: 'none',
                    border: 'none',
                    color: '#2563eb',
                    marginLeft: 8,
                    cursor: 'pointer'
                }}
                title="시험 설정"
            >
                ✏️
            </button>

            {showExamSetting && (
                <div className="dashboard-modal-overlay">
                    <div className="dashboard-modal-card">
                        <h3 style={{ marginTop: 0 }}>시험 설정</h3>
                        <div className="dashboard-modal-row">
                            <label>시험 이름:</label>
                            <input
                                type="text"
                                value={tempExamName}
                                onChange={(e) => setTempExamName(e.target.value)}
                                placeholder="예: 2026학년도 수능"
                            />
                        </div>
                        <div className="dashboard-modal-row">
                            <label>시험 날짜:</label>
                            <input
                                type="date"
                                value={tempExamDate}
                                onChange={(e) => setTempExamDate(e.target.value)}
                            />
                        </div>
                        <div className="dashboard-modal-buttons">
                            <button onClick={saveExamSetting}>저장</button>
                            <button onClick={() => setShowExamSetting(false)}>취소</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default DashboardDday;
