import React, {useState} from 'react';

const categories = ['질문', '정보공유', '스터디', '익명'];

const PostModal = ({onClose, onAddPost}) => {
    const [form, setForm] = useState({
        title: '',
        content: '',
        category: '질문',
        author: '익명',
    });

    const handleChange = (e) => {
        setForm({...form, [e.target.name]: e.target.value});
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
        setForm({title: '', content: '', category: '질문', author: '익명'});
    };

    return (
        <div className="community-modal-overlay">
            <div className="community-modal">
                <button className="community-modal-close" onClick={onClose}>×</button>
                <form onSubmit={handleSubmit}>
                    <div>
                        <input
                            name="title"
                            type="text"
                            placeholder="제목"
                            value={form.title}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
            <textarea
                name="content"
                placeholder="내용"
                value={form.content}
                onChange={handleChange}
                required
            />
                    </div>
                    <div>
                        <select
                            name="category"
                            value={form.category}
                            onChange={handleChange}
                        >
                            {categories.map((cat) => (
                                <option key={cat}>{cat}</option>
                            ))}
                        </select>
                    </div>
                    <button type="submit" className="community-modal-submit">
                        등록
                    </button>
                </form>
            </div>
        </div>
    );
};

export default PostModal;
