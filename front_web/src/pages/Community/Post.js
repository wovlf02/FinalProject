import React, {useState} from 'react';
import PostList from './components/PostList';
import PostModal from './components/PostModal';
import '../../css/Post.css';

const categories = ['전체', '질문', '정보 공유', '스터디', '익명']

const Post = () => {
    const [posts, setPosts] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('전체');
    const [showModal, setShowModal] = useState(false);

    const handleAddPost = (newPost) => {
        setPosts([newPost, ...posts]);
        setShowModal(false);
    };

    const filteredPosts =
        selectedCategory === '전체'
            ? posts
            : posts.filter((post) => post.category === selectedCategory);

    return (
        <div className="community-container">
            <div className="community-header">
                <h2>게시판</h2> {/* ← 여기만 커뮤니티 → 게시판 으로 수정 */}
                <button className="community-write-btn" onClick={() => setShowModal(true)}>
                    ✏️ 글쓰기
                </button>
            </div>
            <div className="community-tabs">
                {categories.map((cat) => (
                    <button
                        key={cat}
                        className={`community-tab-btn${selectedCategory === cat ? ' selected' : ''}`}
                        onClick={() => setSelectedCategory(cat)}
                    >
                        {cat}
                    </button>
                ))}
            </div>
            <PostList posts={filteredPosts} setPosts={setPosts}/>
            {showModal && (
                <PostModal
                    onClose={() => setShowModal(false)}
                    onAddPost={handleAddPost}
                />
            )}
        </div>
    );
}

export default Post;