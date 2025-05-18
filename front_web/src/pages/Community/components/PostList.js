import React from 'react';
import PostItem from './PostItem';

const PostList = ({posts, setPosts}) => {
    const handleLike = (postId) => {
        setPosts(posts.map(post =>
            post.id === postId ? {...post, likes: post.likes + 1} : post
        ));
    };

    const handleAddComment = (postId, comment) => {
        setPosts(posts.map(post =>
            post.id === postId
                ? {
                    ...post,
                    comments: [
                        ...post.comments,
                        {
                            id: Date.now(),
                            text: comment,
                            author: '익명',
                            date: new Date().toISOString(),
                        },
                    ],
                }
                : post
        ));
    };

    return (
        <div className="community-post-list">
            {posts.map((post) => (
                <PostItem
                    key={post.id}
                    post={post}
                    onLike={handleLike}
                    onAddComment={handleAddComment}
                />
            ))}
        </div>
    );
};

export default PostList;
