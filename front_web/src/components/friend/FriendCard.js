import React from 'react';
import '../../css/FriendCard.css';
import api from '../../api/api';

const FriendCard = ({
                        user,
                        type, // 'search' | 'received' | 'sent' | 'friend' | 'blocked'
                        onAccept,
                        onReject,
                        onStartChat,
                        onDelete,
                        onBlock,
                        onUnblock,
                        onRequestSent,
                    }) => {
    // 공통 필드 또는 타입별 분기
    const userId =
        type === 'received' ? user.senderId :
            user.userId;

    const nickname =
        type === 'received' ? user.senderNickname :
            user.nickname;

    const profileImageUrl =
        user.profileImageUrl || '/images/base_profile.png';

    const email =
        type === 'received' ? null : user.email;

    const alreadyFriend = user.alreadyFriend;
    const alreadyRequested = user.alreadyRequested;
    const isBlocked = user.isBlocked;

    const handleSendRequest = async () => {
        try {
            await api.post('/friends/request', { targetUserId: userId });
            alert('친구 요청을 보냈습니다.');
            onRequestSent && onRequestSent(userId);
        } catch (e) {
            alert('친구 요청 실패');
        }
    };

    return (
        <div className="friend-card">
            <div className="friend-left">
                <img
                    src={profileImageUrl}
                    alt={nickname}
                    className="friend-avatar"
                />
                <div className="friend-info">
                    <div className="friend-nickname">{nickname}</div>
                    {email && <div className="friend-email">{email}</div>}
                </div>
            </div>

            <div className="friend-actions">
                {type === 'search' && (
                    alreadyFriend ? (
                        <span className="friend-status-label">이미 친구</span>
                    ) : alreadyRequested ? (
                        <span className="friend-status-label">요청 보냄</span>
                    ) : isBlocked ? (
                        <span className="friend-status-label">차단됨</span>
                    ) : (
                        <button className="btn chat" onClick={handleSendRequest}>친구 요청</button>
                    )
                )}

                {type === 'received' && (
                    <>
                        <button className="btn accept" onClick={() => onAccept(user)}>수락</button>
                        <button className="btn reject" onClick={() => onReject(user)}>거절</button>
                    </>
                )}

                {type === 'sent' && <div className="friend-status-label">보낸 요청</div>}

                {type === 'friend' && (
                    <>
                        <button className="btn chat" onClick={() => onStartChat(user)}>채팅</button>
                        <button className="btn delete" onClick={() => onDelete(user)}>삭제</button>
                        <button className="btn block" onClick={() => onBlock(user)}>차단</button>
                    </>
                )}

                {type === 'blocked' && (
                    <button className="btn unblock" onClick={() => onUnblock(user)}>차단 해제</button>
                )}
            </div>
        </div>
    );
};

export default FriendCard;
