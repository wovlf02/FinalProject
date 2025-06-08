import React, { useEffect, useState } from 'react';
import api from '../../api/api';

const DashboardDday = () => {
    const [examName, setExamName] = useState('');
    const [examDate, setExamDate] = useState(null);
    const [showExamSetting, setShowExamSetting] = useState(false);
    const [tempExamName, setTempExamName] = useState('');
    const [tempExamDate, setTempExamDate] = useState(new Date()); // ✅ 날짜 객체로

    // ✅ D-Day 계산
    const calculateDday = () => {
        if (!examDate) return 0;
        const today = new Date();
        const target = new Date(examDate);
        today.setHours(0, 0, 0, 0);
        target.setHours(0, 0, 0, 0);
        return Math.floor((target - today) / (1000 * 60 * 60 * 24));
    };
    const dDay = calculateDday();

    // ✅ 최초 로딩 시 시험 정보 요청
    useEffect(() => {
        const fetchExam = async () => {
            try {
                const res = await api.post('/dashboard/exams/nearest');
                const { examName, examDate } = res.data?.data;
                setExamName(examName);
                setExamDate(examDate);
            } catch (err) {
                console.error('📅 시험 일정 조회 실패:', err);
            }
        };
        fetchExam();
    }, []);

    // ✅ 시험 설정 열기
    const openExamSetting = () => {
        setTempExamName(examName);
        setTempExamDate(examDate ? new Date(examDate) : new Date()); // ✅ 날짜 객체로 설정
        setShowExamSetting(true);
    };

    // ✅ 서버로 시험 정보 저장
    const saveExamSetting = async () => {
        try {
            await api.post('/dashboard/exams/register', {
                exam_name: tempExamName,
                exam_date: tempExamDate.toISOString().slice(0, 10), // ✅ 'yyyy-MM-dd'
            });
            setExamName(tempExamName);
            setExamDate(tempExamDate.toISOString().slice(0, 10));
            setShowExamSetting(false);
        } catch (err) {
            console.error('📅 시험 일정 저장 실패:', err);
        }
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
                    cursor: 'pointer',
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
                                value={tempExamDate.toISOString().slice(0, 10)} // ✅ 날짜 객체 → yyyy-MM-dd
                                onChange={(e) => setTempExamDate(new Date(e.target.value))}
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
