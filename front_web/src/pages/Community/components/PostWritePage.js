import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../../css/PostWritePage.css'; // 아래 CSS와 경로 맞춰주세요

const categories = ['질문', '정보공유', '스터디', '익명', '취업/진로', '프로젝트'];

const PostWritePage = ({ onAddPost }) => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: '',
    content: '',
    category: categories[0],
    tag: '',
    author: '익명',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.title.trim() || !form.content.trim()) return;
    onAddPost({
      ...form,
      id: Date.now(),
      likes: 0,
      comments: [],
      date: new Date().toISOString(),
    });
    navigate('/community/post');
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
              <option key={cat}>{cat}</option>
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
