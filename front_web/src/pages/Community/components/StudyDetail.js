import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import api from '../../../api/api';

const StudyDetail = () => {
    const {id} = useParams();
    const navigate = useNavigate();
    const [study, setStudy] = useState(null);

    const fetchStudyDetail = async () => {
        try {
            const res = await api.post('/community/posts/sidebar/studies/detail', {
                studyId: Number(id),
            });
            setStudy(res.data);
        } catch (err) {
            console.error('스터디 상세 조회 실패:', err);
            alert('스터디 정보를 불러오는 데 실패했습니다.');
        }
    };

    useEffect(() => {
        fetchStudyDetail();
    }, [id]);

    if (!study) {
        return <div style={{padding: 40, textAlign: 'center'}}>해당 스터디를 찾을 수 없습니다.</div>;
    }

    return (
        <div style={{
            maxWidth: 600,
            margin: '40px auto',
            background: '#fff',
            borderRadius: 14,
            boxShadow: '0 4px 24px rgba(0,0,0,0.08)',
            padding: '36px 40px 32px 40px'
        }}>
            <button
                onClick={() => navigate(-1)}
                style={{
                    color: '#2563eb',
                    background: 'none',
                    border: 'none',
                    marginBottom: 18,
                    cursor: 'pointer'
                }}
            >
                ← 목록으로
            </button>

            <h2 style={{color: '#23272f', marginBottom: 14}}>{study.name}</h2>

            <div style={{marginBottom: 8}}>
        <span style={{
            background: study.tagColor,
            color: '#fff',
            borderRadius: 8,
            padding: '4px 12px',
            fontWeight: 600,
            marginRight: 10
        }}>
          {study.tag}
        </span>
                <span>{study.info}</span>
            </div>

            <div style={{marginTop: 20, color: "#555"}}>
                <b>일정:</b> {study.schedule}
            </div>
            <div style={{marginTop: 6, color: "#555"}}>
                <b>모집 인원:</b> {study.members}명
            </div>
            <div style={{marginTop: 6, color: "#555"}}>
                <b>상태:</b> {study.status}
            </div>
        </div>
    );
};

export default StudyDetail;
