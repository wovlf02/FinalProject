import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import '../css/UnitEvaluationPlanList.css';

const subjects = ["수학", "국어", "영어", "과학", "사회", "한국사"];

function UnitEvaluationPlanList() {
  const [subject, setSubject] = useState("수학");
  const [plans, setPlans] = useState([]);

  useEffect(() => {
    fetch('/api/plan/my')
      .then(res => {
        if (!res.ok) throw new Error('불러오기 실패');
        return res.json();
      })
      .then(data => setPlans(data))
      .catch(() => setPlans([]));
  }, []);

  const filtered = plans.filter(plan => plan.subject === subject);

  return (
    <div className="plan-list-bg">
      <div className="plan-list-container">
        <h2 className="plan-list-title">내 학습 계획 보기</h2>
        <div className="plan-list-select-row">
          <span className="plan-list-label">과목 선택</span>
          <select
            value={subject}
            onChange={e => setSubject(e.target.value)}
            className="plan-list-select"
          >
            {subjects.map(s => <option key={s}>{s}</option>)}
          </select>
        </div>
        <div className="plan-list-cards">
          {filtered.map(plan => {
            // planContent 앞부분만 콘솔에 출력
            const preview = plan.planContent
              ? plan.planContent.slice(0, 80).replace(/\n/g, '\\n')
              : '없음';
            console.log("planContent preview:", preview);

            // 표 마크다운이면 true
            const isMarkdownTable = plan.planContent && plan.planContent.trim().startsWith('|');

            return (
              <div key={plan.id} className="plan-list-card">
                <div className="plan-list-card-title">
                  {plan.grade} {plan.subject} <span className="plan-list-units">[{plan.units}]</span> <span className="plan-list-weeks">({plan.weeks}주)</span>
                </div>
                {isMarkdownTable ? (
                  <ReactMarkdown remarkPlugins={[remarkGfm]}>
                    {plan.planContent}
                  </ReactMarkdown>
                ) : (
                  <div style={{ color: '#888', marginTop: 12 }}>
                    {plan.planContent && plan.planContent.trim()
                      ? '표 마크다운이 아닙니다.'
                      : '표 데이터가 없습니다.'}
                  </div>
                )}
              </div>
            );
          })}
        </div>
        {filtered.length === 0 && (
          <div className="plan-list-empty">
            해당 과목의 저장된 계획이 없습니다.
          </div>
        )}
      </div>
    </div>
  );
}

export default UnitEvaluationPlanList;
