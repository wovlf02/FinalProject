import React, { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { curriculum } from '../components/data/units';
import '../css/UnitEvaluationPlan.css';

const grades = ['1학년', '2학년', '3학년'];
const subjects = ['수학', '국어', '영어', '과학', '사회', '한국사'];
const weeks = [1, 2, 3, 4];

function getSelectedUnits(units, startUnit, endUnit) {
    const startIdx = units.indexOf(startUnit);
    const endIdx = units.indexOf(endUnit);
    if (startIdx === -1 || endIdx === -1 || startIdx > endIdx) return [];
    return units.slice(startIdx, endIdx + 1);
}

// [수정] range 파라미터 추가!
async function fetchPlan(grade, subject, week, selectedUnits) {
    let unitsText = selectedUnits.map(u => `"${u}"`).join(', ');
    let prompt = `
고등학교 ${grade} ${subject} 과목의 ${unitsText} 단원만 포함해서 ${week}주 학습 계획을 마크다운 표로 만들어줘.
반드시 ${unitsText} 단원만 포함하고, 그 외 단원이나 중학교/초등학교 내용은 절대 포함하지 마.
표 아래에는 아무 설명도 붙이지 마.
    `;

    const response = await fetch('/api/plan/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            subject,
            weeks: week,
            prompt,
            range: selectedUnits.join(', ') // 이 부분 추가!
        })
    });
    if (!response.ok) {
        throw new Error('AI 학습계획 생성 실패');
    }
    const text = await response.text();
    return text;
}

const UnitEvaluationPlan = () => {
    const [grade, setGrade] = useState('1학년');
    const [subject, setSubject] = useState('수학');
    const [week, setWeek] = useState(2);
    const [startUnit, setStartUnit] = useState('');
    const [endUnit, setEndUnit] = useState('');
    const [plan, setPlan] = useState('');
    const [loading, setLoading] = useState(false);

    const units = curriculum['고등학교'][grade][subject];

    const handleGenerate = async () => {
        if (!startUnit || !endUnit) {
            alert('시작 단원과 끝 단원을 모두 선택하세요.');
            return;
        }
        const selectedUnits = getSelectedUnits(units, startUnit, endUnit);
        if (selectedUnits.length === 0) {
            alert('올바른 단원 범위를 선택하세요.');
            return;
        }
        setLoading(true);
        setPlan('');
        try {
            const aiPlanText = await fetchPlan(grade, subject, week, selectedUnits);
            setPlan(aiPlanText);
        } catch (e) {
            alert('AI 학습계획 생성에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="plan-container">
            <h2>AI 학습 계획 생성</h2>
            <div className="plan-ai-bar">
                <select value={grade} onChange={e => setGrade(e.target.value)}>
                    {grades.map(g => <option key={g}>{g}</option>)}
                </select>
                <select value={subject} onChange={e => setSubject(e.target.value)}>
                    {subjects.map(s => <option key={s}>{s}</option>)}
                </select>
                <select value={week} onChange={e => setWeek(Number(e.target.value))}>
                    {weeks.map(w => <option key={w} value={w}>{w}주</option>)}
                </select>
                <select value={startUnit} onChange={e => setStartUnit(e.target.value)}>
                    <option value="">시작 단원</option>
                    {units.map(u => <option key={u}>{u}</option>)}
                </select>
                <select value={endUnit} onChange={e => setEndUnit(e.target.value)}>
                    <option value="">끝 단원</option>
                    {units.map(u => <option key={u}>{u}</option>)}
                </select>
                <button className="plan-generate-btn" onClick={handleGenerate} disabled={loading}>
                    {loading ? '생성 중...' : '계획 생성하기'}
                </button>
            </div>
            {plan && (
                <div className="ai-plan-result">
                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{plan}</ReactMarkdown>
                </div>
            )}
        </div>
    );
};

export default UnitEvaluationPlan;
