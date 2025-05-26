import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../../api/api';

const initialForm = {
    name: '',
    info: '',
    schedule: '',
    status: '모집중',
    tag: '',
    color: '#e9d8fd',
    tagColor: '#a78bfa',
    members: '', // 모집 인원
};

const StudyCreatePage = () => {
    const [form, setForm] = useState(initialForm);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const { name, info, schedule, tag, members } = form;
        if (!name || !info || !schedule || !tag || !members) {
            alert('모든 필수 항목을 입력해주세요.');
            return;
        }

        try {
            await api.post('/community/posts/sidebar/studies/create', {
                ...form,
                members: parseInt(form.members, 10),
            });
            navigate('/study');
        } catch (err) {
            console.error('스터디 생성 실패:', err);
            alert('스터디 생성 중 오류가 발생했습니다.');
        }
    };

    return (
        <div
            style={{
                maxWidth: 600,
                margin: '40px auto',
                background: '#fff',
                borderRadius: 14,
                boxShadow: '0 4px 24px rgba(0,0,0,0.08)',
                padding: '36px 40px 32px 40px',
            }}
        >
            <h2 style={{ color: '#23272f', marginBottom: 18 }}>스터디 만들기</h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
                <label>
                    <span style={{ fontWeight: 500 }}>스터디 이름 *</span>
                    <input
                        type="text"
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        placeholder="스터디 이름을 입력하세요"
                        required
                        style={inputStyle}
                    />
                </label>
                <label>
                    <span style={{ fontWeight: 500 }}>스터디 소개 *</span>
                    <textarea
                        name="info"
                        value={form.info}
                        onChange={handleChange}
                        placeholder="스터디 소개 및 간단 설명"
                        required
                        rows={3}
                        style={{ ...inputStyle, resize: 'vertical' }}
                    />
                </label>
                <label>
                    <span style={{ fontWeight: 500 }}>일정/시간 *</span>
                    <input
                        type="text"
                        name="schedule"
                        value={form.schedule}
                        onChange={handleChange}
                        placeholder="예: 매주 수요일 19:00"
                        required
                        style={inputStyle}
                    />
                </label>
                <label>
                    <span style={{ fontWeight: 500 }}>모집 인원 *</span>
                    <input
                        type="number"
                        name="members"
                        min={1}
                        value={form.members}
                        onChange={handleChange}
                        placeholder="예: 8"
                        required
                        style={inputStyle}
                    />
                </label>
                <label>
                    <span style={{ fontWeight: 500 }}>상태 *</span>
                    <select
                        name="status"
                        value={form.status}
                        onChange={handleChange}
                        style={inputStyle}
                    >
                        <option value="모집중">모집중</option>
                        <option value="마감">마감</option>
                    </select>
                </label>
                <label>
                    <span style={{ fontWeight: 500 }}>태그 *</span>
                    <input
                        type="text"
                        name="tag"
                        value={form.tag}
                        onChange={handleChange}
                        placeholder="예: 알고리즘, 스터디"
                        required
                        style={inputStyle}
                    />
                </label>
                <div style={{ display: 'flex', gap: 18 }}>
                    <label style={{ flex: 1 }}>
                        <span style={{ fontWeight: 500 }}>배경색</span>
                        <input
                            type="color"
                            name="color"
                            value={form.color}
                            onChange={handleChange}
                            style={{ marginLeft: 12, verticalAlign: 'middle' }}
                        />
                    </label>
                    <label style={{ flex: 1 }}>
                        <span style={{ fontWeight: 500 }}>태그색</span>
                        <input
                            type="color"
                            name="tagColor"
                            value={form.tagColor}
                            onChange={handleChange}
                            style={{ marginLeft: 12, verticalAlign: 'middle' }}
                        />
                    </label>
                </div>
                <button
                    type="submit"
                    style={{
                        marginTop: 18,
                        background: '#2563eb',
                        color: '#fff',
                        border: 'none',
                        borderRadius: 8,
                        padding: '12px 0',
                        fontWeight: 600,
                        fontSize: '1.07rem',
                        cursor: 'pointer',
                    }}
                >
                    생성하기
                </button>
            </form>
        </div>
    );
};

const inputStyle = {
    width: '100%',
    marginTop: 6,
    marginBottom: 10,
    padding: '10px 13px',
    border: '1px solid #e5e7eb',
    borderRadius: 7,
    fontSize: '1rem',
};

export default StudyCreatePage;
