import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const SelectUnit = () => {
  const location = useLocation();
  let exam = location.state?.exam;
  const handleCompleteUnit = location.state?.handleCompleteUnit;

  // units가 없으면 localStorage/testStatus에서 찾아서 보완
  if (!exam.units) {
    // testStatus를 localStorage나 context에서 불러오거나, 
    // 혹은 units를 직접 하드코딩해서라도 넣어줘야 함
    // 예시:
    const allExams = [
      { title: '수학 수능', units: ['1단원', '2단원', '3단원'] },
      { title: '영어 수능', units: ['1단원', '2단원', '3단원', '4단원'] },
      { title: '국어 수능', units: ['1단원', '2단원', '3단원', '4단원', '5단원'] },
    ];
    const found = allExams.find(e => e.title === exam.title);
    if (found) exam.units = found.units;
  }

  // 과목명 동적으로 추출
  const subject = exam?.subject || (exam?.title ? exam.title.split(' ')[0] : '수학');

  const [unitList, setUnitList] = useState([]); // 단원명 리스트
  const [selectedUnit, setSelectedUnit] = useState(''); // 선택된 단원명
  const [isStudying, setIsStudying] = useState(false);

  // 단원명 리스트 불러오기 (subject 기준)
  useEffect(() => {
    if (!exam) return;
    axios.get(`/api/units?subject=${subject}`)
      .then(res => {
        setUnitList(res.data);
        setSelectedUnit(res.data[0] || '');
      })
      .catch(() => setUnitList([]));
  }, [exam, subject]);

  if (!exam) {
    return <div style={{ textAlign: 'center', marginTop: 80, fontSize: 20 }}>잘못된 접근입니다. 시험을 먼저 선택하세요.</div>;
  }

  const handleUnitSelect = () => {
    setIsStudying(true);
    alert(`${selectedUnit} 공부를 시작합니다!`);
  };

  const handleComplete = () => {
    if (!isStudying) {
      alert('먼저 공부 시작을 눌러주세요!');
      return;
    }
    const subject = exam.subject || exam.title.split(' ')[0];
    let completed = JSON.parse(localStorage.getItem('completedUnits') || '{}');
    completed[subject] = completed[subject] || [];
    if (!completed[subject].includes(selectedUnit)) {
      completed[subject].push(selectedUnit);
      localStorage.setItem('completedUnits', JSON.stringify(completed));
      alert(`${selectedUnit} 공부 완료!`);
    } else {
      alert('이미 완료한 단원입니다!');
    }
    window.history.back();
  };

  // 완료 단원 수/진도율 계산
  const completedArr = JSON.parse(localStorage.getItem('completedUnits') || '{}')[subject] || [];
  const completedCount = completedArr.length;
  const totalCount = unitList.length;
  const percent = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;

  return (
    <div style={{ background: '#fff', minHeight: '100vh', padding: 0 }}>
      <div style={{ maxWidth: 1000, margin: '0 auto', padding: '60px 0' }}>
        {/* 상단 과목명 */}
        <div style={{ textAlign: 'center', fontWeight: 800, fontSize: 38, marginBottom: 48, letterSpacing: '-1px', color: '#111' }}>
          {subject}
        </div>
        {/* 카드 2개 나란히 */}
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'flex-start',
          gap: 48,
          flexWrap: 'wrap'
        }}>
          {/* 시험 설정 카드 */}
          <div style={{
            background: '#fff',
            borderRadius: 18,
            boxShadow: '0 4px 24px #1112',
            border: '2px solid #111',
            padding: 48,
            width: 420,
            minWidth: 320,
            display: 'flex',
            flexDirection: 'column',
            gap: 36,
            alignItems: 'center'
          }}>
            <div style={{ fontWeight: 700, fontSize: 28, marginBottom: 16, color: '#111' }}>시험 설정</div>
            <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 32 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
                <label style={{ fontWeight: 500, width: 110, color: '#222', fontSize: 20 }}>과목</label>
                <span style={{ fontWeight: 600, fontSize: 22, color: '#111' }}>{subject}</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
                <label style={{ fontWeight: 500, width: 110, color: '#222', fontSize: 20 }}>단원 선택</label>
                <select value={selectedUnit} onChange={e => setSelectedUnit(e.target.value)}
                  style={{
                    flex: 1,
                    padding: '14px 18px',
                    borderRadius: 10,
                    border: '1.5px solid #111',
                    fontSize: 20,
                    minWidth: 0,
                    background: '#fff',
                    color: '#111'
                  }}>
                  {unitList.map((unit, idx) => (
                    <option key={idx} value={unit}>{unit}</option>
                  ))}
                </select>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 18, width: '100%' }}>
              <button
                onClick={handleUnitSelect}
                style={{
                  flex: 1,
                  padding: '18px 0',
                  borderRadius: 10,
                  border: 'none',
                  background: '#111',
                  color: '#fff',
                  fontWeight: 700,
                  fontSize: 22,
                  letterSpacing: '1px',
                  boxShadow: '0 2px 8px #1112',
                  transition: 'background 0.2s',
                  cursor: 'pointer'
                }}>
                {selectedUnit ? '공부 시작' : '단원을 선택하세요'}
              </button>
              <button
                onClick={handleComplete}
                disabled={!selectedUnit || !isStudying}
                style={{
                  flex: 1,
                  padding: '18px 0',
                  borderRadius: 10,
                  border: 'none',
                  background: (!selectedUnit || !isStudying) ? '#e0e0e0' : '#111',
                  color: (!selectedUnit || !isStudying) ? '#888' : '#fff',
                  fontWeight: 700,
                  fontSize: 22,
                  letterSpacing: '1px',
                  boxShadow: '0 2px 8px #1112',
                  transition: 'background 0.2s',
                  cursor: (!selectedUnit || !isStudying) ? 'not-allowed' : 'pointer'
                }}>
                공부 완료
              </button>
            </div>
          </div>
          {/* 공부 완료 박스 */}
          <div style={{
            background: '#fff',
            borderRadius: 18,
            boxShadow: '0 4px 24px #1112',
            border: '2px solid #111',
            padding: 40,
            width: 260,
            minWidth: 180,
            height: 340,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 18
          }}>
            <div style={{ fontWeight: 700, fontSize: 22, marginBottom: 10, color: '#111' }}>공부 완료</div>
            <div style={{ fontSize: 54, fontWeight: 800, color: '#111', marginBottom: 6 }}>{completedCount}</div>
            <div style={{ fontSize: 18, color: '#888', marginBottom: 12 }}>단원 / 총 {totalCount}개</div>
            <div style={{ width: '100%', background: '#e0e0e0', borderRadius: 8, height: 14, margin: '18px 0' }}>
              <div style={{
                width: `${percent}%`,
                background: '#111',
                height: '100%',
                borderRadius: 8,
                transition: 'width 0.3s'
              }} />
            </div>
            <div style={{ fontSize: 20, fontWeight: 600, color: '#111' }}>진도율 {percent}%</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectUnit;
