import React, {useState} from 'react';
import {useLocation} from 'react-router-dom';
import '../css/UnitEvaluationFeedback.css';

// AI 피드백 더미 데이터 생성 함수
const generateFeedback = () => {
    const strengths = [
        '함수 개념 이해도가 높습니다.',
        '공식 적용이 빠르고 정확합니다.',
        '문제 해결 과정이 체계적입니다.'
    ];
    const weaknesses = [
        '계산 실수가 잦습니다.',
        '시간 관리가 필요합니다.',
        '복잡한 문제 접근이 어렵습니다.'
    ];
    const recommendations = [
        '매일 10문제 계산 연습',
        '시간 제한 두고 문제 풀기',
        '고난도 문제 주별 2회 풀기'
    ];

    return {
        overall: '전반적으로 좋은 성적이지만, 계산 실수와 시간 관리에 주의가 필요합니다.',
        strengths: strengths.sort(() => Math.random() - 0.5).slice(0, 2),
        weaknesses: weaknesses.sort(() => Math.random() - 0.5).slice(0, 2),
        recommendations: recommendations.sort(() => Math.random() - 0.5).slice(0, 2)
    };
};

const UnitEvaluationFeedback = () => {
    const {state} = useLocation();
    const {unitName, subject, level} = state || {};
    const [feedback] = useState(generateFeedback());
    const [checked, setChecked] = useState([]);

    const handleCheck = (idx) => {
        setChecked(prev =>
            prev.includes(idx)
                ? prev.filter(i => i !== idx)
                : [...prev, idx]
        );
    };

    return (
        <div className="feedback-container">
            <h2>AI 학습 피드백 - {unitName || '단원명'}</h2>
            <div className="feedback-meta">
                <span>과목: {subject || '-'}</span>
                <span>난이도: {level || '-'}</span>
            </div>

            <div className="feedback-section overall">
                <h3>📊 종합 평가</h3>
                <p>{feedback.overall}</p>
            </div>

            <div className="feedback-grid">
                <div className="feedback-section strengths">
                    <h3>✅ 강점</h3>
                    <ul>
                        {feedback.strengths.map((item, i) => (
                            <li key={i}>{item}</li>
                        ))}
                    </ul>
                </div>

                <div className="feedback-section weaknesses">
                    <h3>⚠️ 약점</h3>
                    <ul>
                        {feedback.weaknesses.map((item, i) => (
                            <li key={i}>{item}</li>
                        ))}
                    </ul>
                </div>

                <div className="feedback-section recommendations">
                    <h3>📌 개선 방안</h3>
                    <ul>
                        {feedback.recommendations.map((item, i) => (
                            <li key={i}>
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={checked.includes(i)}
                                        onChange={() => handleCheck(i)}
                                    />
                                    {item}
                                </label>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default UnitEvaluationFeedback;
