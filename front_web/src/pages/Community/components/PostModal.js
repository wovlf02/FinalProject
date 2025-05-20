import React, { useState } from 'react';
import '../../../css/PostModal.css';

const categories = ['질문', '정보공유', '스터디', '익명'];

const PostModal = ({ onClose, onAddPost }) => {
  const [form, setForm] = useState({
    title: '',
    content: '',
    category: '질문',
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
    setForm({ title: '', content: '', category: '질문', author: '익명' });
  };

  return (
    <div className="postmodal-overlay">
      <div className="postmodal-container">
        <button className="postmodal-close" onClick={onClose} title="닫기">
          ×
        </button>
        <h2 className="postmodal-title">글쓰기</h2>
        <form onSubmit={handleSubmit} className="postmodal-form">
          <div className="postmodal-field">
            <label htmlFor="category">분류</label>
            <select
              id="category"
              name="category"
              value={form.category}
              onChange={handleChange}
              className="postmodal-select"
            >
              {categories.map((cat) => (
                <option key={cat}>{cat}</option>
              ))}
            </select>
          </div>
          <div className="postmodal-field">
            <label htmlFor="title">제목</label>
            <input
              id="title"
              name="title"
              type="text"
              placeholder="제목을 입력하세요"
              value={form.title}
              onChange={handleChange}
              required
              className="postmodal-input"
              maxLength={60}
            />
          </div>
          <div className="postmodal-field">
            <label htmlFor="content">내용</label>
            <textarea
              id="content"
              name="content"
              placeholder="내용을 입력하세요"
              value={form.content}
              onChange={handleChange}
              required
              className="postmodal-textarea"
              rows={8}
            />
          </div>
          <button type="submit" className="postmodal-submit">
            등록
          </button>
        </form>
      </div>
    </div>
  );
};

export default PostModal;
