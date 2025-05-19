import React, {useState} from 'react';

const PostForm = ({onAddPost}) => {
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        category: '질문',
        author: '익명'
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!formData.title || !formData.content) return;

        const newPost = {
            ...formData,
            id: Date.now(),
            likes: 0,
            comments: [],
            date: new Date().toISOString()
        };

        onAddPost(newPost);
        setFormData({title: '', content: '', category: '질문', author: '익명'});
    };

    return (
        <form onSubmit={handleSubmit} className="post-form">
            <input
                type="text"
                placeholder="제목"
                value={formData.title}
                onChange={(e) => setFormData({...formData, title: e.target.value})}
            />
            <textarea
                placeholder="내용"
                value={formData.content}
                onChange={(e) => setFormData({...formData, content: e.target.value})}
            />
            <select
                value={formData.category}
                onChange={(e) => setFormData({...formData, category: e.target.value})}
            >
                <option value="질문">질문</option>
                <option value="정보공유">정보공유</option>
                <option value="스터디">스터디</option>
            </select>
            <button type="submit">글 등록</button>
        </form>
    );
};

export default PostForm;
