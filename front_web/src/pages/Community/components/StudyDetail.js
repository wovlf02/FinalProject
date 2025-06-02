import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../../api/api';

const StudyDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [study, setStudy] = useState(null);
    const [user, setUser] = useState(null);
    const [applications, setApplications] = useState([]);

    const fetchStudyDetail = async () => {
        try {
            const res = await api.get(`/community/posts/sidebar/studies/${id}`);
            setStudy(res.data);
        } catch (err) {
            console.error('스터디 상세 조회 실패:', err);
            alert('스터디 정보를 불러오는 데 실패했습니다.');
        }
    };

    const fetchCurrentUser = async () => {
        try {
            const res = await api.get('/users/me');
            setUser(res.data.data);
        } catch (err) {
            console.error('사용자 정보 조회 실패:', err);
        }
    };

    useEffect(() => {
        fetchCurrentUser();
        fetchStudyDetail();
    }, [id]);

    useEffect(() => {
        if (study && user) {
            console.log('📌 현재 로그인한 사용자:', user);
            console.log('📌 스터디 상세 정보:', study);
            console.log('📌 creator_id === user_id:', study.creator_id, user.userId, study.creator_id === user.userId);
            console.log('📌 참가자 목록:', study.participants);
            console.log('📌 isParticipant:', study.participants?.includes(user.userId));
            console.log('📌 current_members / max:', study.current_members, '/', study.members);

            if (study.creator_id === user.userId) {
                console.log('✅ 현재 사용자는 이 스터디의 생성자입니다. 신청자 목록을 가져옵니다.');
                fetchApplications();
            }
        }
    }, [study, user]);

    const fetchApplications = async () => {
        try {
            const res = await api.get(`/community/posts/sidebar/studies/${id}/applications`);
            setApplications(res.data.data || []);
            console.log('📥 신청자 목록 응답:', res.data);
        } catch (err) {
            console.error('❌ 신청자 목록 조회 실패:', err);
        }
    };


    const handleApply = async () => {
        try {
            await api.post('/community/posts/sidebar/studies/apply', {
                study_id: Number(id)
            });
            alert('스터디 참여 신청이 완료되었습니다.');
        } catch (err) {
            console.error('스터디 신청 실패:', err);
            alert('신청 중 오류가 발생했습니다.');
        }
    };

    const handleApproval = async (applicantId, approve) => {
        try {
            await api.post(`/community/posts/sidebar/studies/approve`, {
                study_id: Number(id),
                applicant_id: applicantId,
                approve
            });
            console.log("신청자 id: ", applicantId);
            alert(`신청이 ${approve ? '수락' : '거절'}되었습니다.`);
            fetchApplications(); // 목록 갱신
            fetchStudyDetail(); // 참여자 수 갱신
        } catch (err) {
            console.error('승인 처리 실패:', err);
            alert('처리 중 오류가 발생했습니다.');
        }
    };

    useEffect(() => {
        fetchCurrentUser();
        fetchStudyDetail();
    }, [id]);

    useEffect(() => {
        if (study && user && study.creator_id === user.user_id) {
            fetchApplications();
        }
    }, [study, user]);

    if (!study || !user) {
        return <div style={{ padding: 40, textAlign: 'center' }}>해당 스터디를 찾을 수 없습니다.</div>;
    }

    const isCreator = study.creator_id === user.user_id;
    console.log("스터디 생성자:", study.creator_id);
    const isParticipant = study.participants?.some(p => p.user_id === user.user_id);
    const isFull = study.current_members >= study.members;
    console.log("스터디 현재 참여자 수:", study);

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

            <h2 style={{ color: '#23272f', marginBottom: 14 }}>{study.name}</h2>

            <div style={{ marginBottom: 8 }}>
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

            <div style={{ marginTop: 20, color: "#555" }}>
                <b>일정:</b> {study.schedule}
            </div>
            <div style={{ marginTop: 6, color: "#555" }}>
                <b>모집 인원:</b> {study.current_members} / {study.members}명
            </div>
            <div style={{ marginTop: 6, color: "#555" }}>
                <b>상태:</b> {study.status}
            </div>

            {!isCreator && !isParticipant && !isFull && (
                <button
                    onClick={handleApply}
                    style={{
                        marginTop: 24,
                        background: '#2563eb',
                        color: '#fff',
                        border: 'none',
                        borderRadius: 8,
                        padding: '10px 20px',
                        fontWeight: 600,
                        fontSize: '1rem',
                        cursor: 'pointer'
                    }}
                >
                    신청하기
                </button>
            )}

            {/* ✅ 생성자에게만 보이는 신청자 목록 */}
            {isCreator && (
                <div style={{ marginTop: 36 }}>
                    <h3 style={{ marginBottom: 12 }}>참여 신청 내역</h3>
                    {applications.length === 0 ? (
                        <div style={{ color: '#888' }}>신청한 사용자가 없습니다.</div>
                    ) : (
                        <ul style={{ listStyle: 'none', padding: 0 }}>
                            {applications.map(app => (
                                <li key={app.user_id} style={{
                                    borderBottom: '1px solid #eee',
                                    padding: '10px 0',
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center'
                                }}>
                                    <span>{app.nickname}</span>
                                    <div style={{ display: 'flex', gap: 10 }}>
                                        <button onClick={() => handleApproval(app.user_id, true)}>수락</button>
                                        <button onClick={() => handleApproval(app.user_id, false)}>거절</button>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            )}
        </div>
    );
};

export default StudyDetail;
