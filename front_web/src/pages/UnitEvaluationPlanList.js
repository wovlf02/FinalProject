import React, { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import '../css/UnitEvaluationPlanList.css';

const subjects = ["수학", "국어", "영어", "과학", "사회", "한국사"];

function UnitEvaluationPlanList() {
  const [subject, setSubject] = useState("수학");
  const [plans, setPlans] = useState([]);
  const [editId, setEditId] = useState(null);
  const [editContent, setEditContent] = useState("");

  // 계획 목록 불러오기
  useEffect(() => {
    fetchPlans();
  }, []);

  const fetchPlans = () => {
    fetch('/api/plan/my')
      .then(res => {
        if (!res.ok) throw new Error('불러오기 실패');
        return res.json();
      })
      .then(data => setPlans(data))
      .catch(() => setPlans([]));
  };

  // 계획 삭제
  const handleDelete = (planId) => {
    if (window.confirm("정말 삭제하시겠습니까?")) {
      fetch(`/api/plan/${planId}`, { method: "DELETE" })
        .then(res => {
          if (!res.ok) throw new Error("삭제 실패");
          setPlans(plans => plans.filter(plan => plan.id !== planId));
        })
        .catch(err => alert(err.message));
    }
  };

  // 계획 완료 체크/해제
  const handleCheck = (planId, checked) => {
    fetch(`/api/plan/${planId}/check?checked=${checked}`, { method: "PATCH" })
      .then(res => {
        if (!res.ok) throw new Error("상태 변경 실패");
        setPlans(plans =>
          plans.map(plan =>
            plan.id === planId ? { ...plan, checked } : plan
          )
        );
      })
      .catch(err => alert(err.message));
  };

  // 계획 내용 수정 저장
  const handleSave = (planId) => {
    fetch(`/api/plan/${planId}/content`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ planContent: editContent })
    })
      .then(res => {
        if (!res.ok) throw new Error("수정 실패");
        setEditId(null);
        setEditContent("");
        fetchPlans();
      })
      .catch(err => alert(err.message));
  };

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
            const isMarkdownTable = plan.planContent && plan.planContent.trim().startsWith('|');
            const checked = !!plan.checked;
            const isEditing = editId === plan.id;

            return (
              <div
                key={plan.id}
                className={`plan-list-card${checked ? " checked" : ""}`}
              >
                <div className="plan-list-card-header-row">
                  <input
                    type="checkbox"
                    checked={checked}
                    onChange={e => handleCheck(plan.id, e.target.checked)}
                  />
                  <div
                    className={`plan-list-card-title${checked ? " checked" : ""}`}
                  >
                    {plan.grade} {plan.subject}
                    <span className="plan-list-units">[{plan.units}]</span>
                    <span className="plan-list-weeks">({plan.weeks}주)</span>
                  </div>
                  <button
                    className="plan-list-delete-btn"
                    onClick={() => handleDelete(plan.id)}
                  >
                    삭제
                  </button>
                  <button
                    className="plan-list-edit-btn"
                    onClick={() => {
                      setEditId(plan.id);
                      setEditContent(plan.planContent || "");
                    }}
                  >
                    수정
                  </button>
                </div>
                {isEditing ? (
                  <div style={{ marginTop: 12 }}>
                    <textarea
                      className="plan-list-edit-textarea"
                      value={editContent}
                      onChange={e => setEditContent(e.target.value)}
                      rows={10}
                    />
                    <div style={{ marginTop: 8, display: "flex", gap: 8 }}>
                      <button
                        className="plan-list-edit-save-btn"
                        onClick={() => handleSave(plan.id)}
                      >
                        저장
                      </button>
                      <button
                        className="plan-list-edit-cancel-btn"
                        onClick={() => setEditId(null)}
                      >
                        취소
                      </button>
                    </div>
                  </div>
                ) : isMarkdownTable ? (
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
