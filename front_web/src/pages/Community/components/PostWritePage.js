import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../../api/api';
import '../../../css/PostWritePage.css';

// ✅ 서버의 Enum 이름과 라벨을 매핑한 배열
const categories = [
    { value: 'QUESTION', label: '질문' },
    { value: 'INFO', label: '정보 공유' },
    { value: 'STUDY', label: '스터디' },
    { value: 'ANONYMOUS', label: '익명' },
    { value: 'GENERAL', label: '일반' },
    { value: 'NOTICE', label: '공지사항' },
];

const PostWritePage = () => {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        title: '',
        content: '',
        category: categories[0].value, // ✅ Enum name: "QUESTION"
        tag: ''
    });

    const [file, setFile] = useState(null);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!form.title.trim() || !form.content.trim()) {
            alert('제목과 내용을 모두 입력해주세요.');
            return;
        }

        try {
            const formData = new FormData();
            formData.append('title', form.title);
            formData.append('content', form.content);
            formData.append('category', form.category); // 서버 enum과 일치하는 문자열
            formData.append('tag', form.tag || ''); // tag는 nullable이므로 빈 문자열 허용

            if (file) {
                formData.append('file', file); // 단일 파일 전송
            }

            console.log("📤 게시글 작성 요청 데이터:", {
                ...form,
                file: file ? file.name : null
            });

            await api.post('/community/posts/create', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            navigate('/community/post');
        } catch (err) {
            console.error('❌ 게시글 등록 실패:', err);
            alert('게시글 등록에 실패했습니다.');
        }
    };

    return (
        <div className="write-page-container">
            <div className="write-form-title">새 글 작성</div>
            <form className="write-form" onSubmit={handleSubmit}>
                <div className="write-form-row">
                    <label className="write-form-label" htmlFor="title">제목 *</label>
                    <input
                        className="write-form-input"
                        id="title"
                        name="title"
                        placeholder="제목을 입력하세요"
                        value={form.title}
                        onChange={handleChange}
                        maxLength={60}
                        required
                    />
                </div>

                <div className="write-form-row">
                    <label className="write-form-label" htmlFor="category">카테고리 *</label>
                    <select
                        className="write-form-select"
                        id="category"
                        name="category"
                        value={form.category}
                        onChange={handleChange}
                        required
                    >
                        {categories.map((cat) => (
                            <option key={cat.value} value={cat.value}>
                                {cat.label}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="write-form-row">
                    <label className="write-form-label" htmlFor="tag">태그</label>
                    <input
                        className="write-form-input"
                        id="tag"
                        name="tag"
                        placeholder="태그를 입력하세요 (쉼표로 구분)"
                        value={form.tag}
                        onChange={handleChange}
                    />
                </div>

                <div className="write-form-row">
                    <label className="write-form-label" htmlFor="content">내용 *</label>
                    <textarea
                        className="write-form-textarea"
                        id="content"
                        name="content"
                        placeholder="내용을 입력하세요"
                        value={form.content}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="write-form-row">
                    <label className="write-form-label" htmlFor="file">첨부파일</label>
                    <input
                        type="file"
                        id="file"
                        name="file"
                        onChange={handleFileChange}
                    />
                </div>

                <div className="write-form-actions">
                    <button
                        type="button"
                        className="write-form-cancel"
                        onClick={() => navigate(-1)}
                    >
                        취소
                    </button>
                    <button type="submit" className="write-form-submit">
                        등록
                    </button>
                </div>
            </form>
        </div>
    );
};

export default PostWritePage;
