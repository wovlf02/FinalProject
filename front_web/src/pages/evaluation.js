import React from 'react';

const Evaluation = () => {
  return (
    <div style={{ padding: '20px', width: '100%' }}>
      <h1>단원 평가</h1>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
        <button>+ 새 평가 추가</button>
        <div>
          <button>일정</button>
          <button>시험 보기</button>
          <button>학습 계획</button>
          <button>AI 피드백</button>
        </div>
      </div>
      <div>
        <h2>시험 준비 현황</h2>
        <div style={{ display: 'flex', gap: '20px' }}>
          {/* 시험 준비 카드 */}
          <div style={{ border: '1px solid #ddd', padding: '10px', width: '200px' }}>
            <h3>수학 중간고사</h3>
            <p>D-7</p>
            <p>학습 진도: 75%</p>
            <button>상세보기</button>
          </div>
          {/* 다른 카드들도 동일한 구조로 추가 */}
        </div>
      </div>
      <div>
        <h2>최근 평가 결과</h2>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th>과목</th>
              <th>평가명</th>
              <th>점수</th>
              <th>평균</th>
              <th>석차</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>수학</td>
              <td>2학기 중간고사</td>
              <td>95점</td>
              <td>82점</td>
              <td>상위 15%</td>
            </tr>
            {/* 다른 데이터도 동일한 구조로 추가 */}
          </tbody>
        </table>
      </div>
      <div>
        <h2>성적 분석</h2>
        <div>
          {/* 성적 분석 그래프는 라이브러리를 사용하여 추가 */}
          <p>그래프 자리</p>
        </div>
      </div>
    </div>
  );
};

export default Evaluation;
