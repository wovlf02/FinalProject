import React, {useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';

const subjects = ['국어', '수학', '영어', '과학', '사회', '기타'];
const levels = ['쉬움', '보통', '어려움'];

const UnitEvaluation = () => {
    const location = useLocation();
    const navigate = useNavigate();
    // 캠스터디에서 전달된 단원명
    const {unitName: initialUnitName} = location.state || {};

    // 단원명이 없으면 입력, 있으면 표시만
    const [unitName, setUnitName] = useState(initialUnitName || '');

    const [selectedSubject, setSelectedSubject] = useState(subjects[0]);
    const [selectedLevel, setSelectedLevel] = useState(levels[0]);

    const isUnitNameInput = !initialUnitName; // 단원명이 없으면 입력창

    const handleStartEvaluation = () => {
        // 평가 시작 페이지로 이동
        navigate('/unit-evaluation/start', {
            state: {
                unitName,
                subject: selectedSubject,
                level: selectedLevel,
            }
        });
    };

    return (
        <div style={{padding: '2rem', textAlign: 'center', maxWidth: 400, margin: '0 auto'}}>
            <h1>단원평가</h1>
            <div style={{margin: '1.5rem 0'}}>
                {isUnitNameInput ? (
                    <label>
                        <span style={{display: 'block', marginBottom: 8, fontWeight: 600}}>단원명 입력</span>
                        <input
                            type="text"
                            value={unitName}
                            onChange={e => setUnitName(e.target.value)}
                            placeholder="예: 3단원 함수"
                            style={{
                                width: '100%',
                                padding: '10px',
                                fontSize: '1.1rem',
                                borderRadius: '5px',
                                border: '1px solid #ccc'
                            }}
                        />
                    </label>
                ) : (
                    <div style={{fontWeight: 600, fontSize: 20, marginBottom: 8}}>{unitName}</div>
                )}
            </div>
            <div style={{margin: '1.5rem 0'}}>
                <label>
                    <span style={{display: 'block', marginBottom: 8, fontWeight: 600}}>과목 선택</span>
                    <select
                        value={selectedSubject}
                        onChange={e => setSelectedSubject(e.target.value)}
                        style={{
                            width: '100%',
                            padding: '10px',
                            fontSize: '1.1rem',
                            borderRadius: '5px',
                            border: '1px solid #ccc'
                        }}
                    >
                        {subjects.map(subject => (
                            <option key={subject} value={subject}>{subject}</option>
                        ))}
                    </select>
                </label>
            </div>
            <div style={{margin: '1.5rem 0'}}>
                <label>
                    <span style={{display: 'block', marginBottom: 8, fontWeight: 600}}>난이도 선택</span>
                    <select
                        value={selectedLevel}
                        onChange={e => setSelectedLevel(e.target.value)}
                        style={{
                            width: '100%',
                            padding: '10px',
                            fontSize: '1.1rem',
                            borderRadius: '5px',
                            border: '1px solid #ccc'
                        }}
                    >
                        {levels.map(level => (
                            <option key={level} value={level}>{level}</option>
                        ))}
                    </select>
                </label>
            </div>
            <button
                onClick={handleStartEvaluation}
                disabled={isUnitNameInput && unitName.trim() === ''}
                style={{
                    padding: '12px 32px',
                    background: '#4CAF50',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    fontSize: '1.1rem',
                    cursor: (isUnitNameInput && unitName.trim() === '') ? 'not-allowed' : 'pointer',
                    marginTop: '1.5rem'
                }}
            >
                평가 시작
            </button>
            <div style={{marginTop: '2.5rem'}}>
                <button
                    onClick={() => navigate('/')}
                    style={{
                        padding: '10px 20px',
                        background: '#888',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    메인으로 돌아가기
                </button>
            </div>
        </div>
    );
};

export default UnitEvaluation;
