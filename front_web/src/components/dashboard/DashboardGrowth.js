import React, { useEffect, useState } from 'react';
import api from '../../api/api';

const DashboardGrowth = () => {
  const [growthList, setGrowthList] = useState([]);

  useEffect(() => {
    const fetchGrowthData = async () => {
      try {
        const res = await api.post('/dashboard/stats/weekly'); // ✅ 실제 연동 API
        const subjectGrowth = res.data?.subjectGrowth || []; // ✅ 서버 응답 구조에 맞춰 파싱
        setGrowthList(subjectGrowth);
      } catch (err) {
        console.error('주간 성장률 조회 실패:', err);
      }
    };

    fetchGrowthData();
  }, []);

  return (
      <div className="dashboard-card dashboard-growth-card">
        <div style={{ fontWeight: 600, marginBottom: 8 }}>주간 성장률</div>
        {growthList.length === 0 ? (
            <div style={{ color: '#999' }}>이번 주 학습 데이터가 없습니다.</div>
        ) : (
            growthList.map((g, i) => (
                <div key={i} style={{ marginBottom: 10 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>{g.subject}</span>
                    <span style={{ color: '#2563eb', fontWeight: 600 }}>+{g.rate}%</span>
                  </div>
                  <div className="dashboard-growth-bar-bg">
                    <div className="dashboard-growth-bar" style={{ width: `${g.rate * 2}%` }} />
                  </div>
                </div>
            ))
        )}
      </div>
  );
};

export default DashboardGrowth;
