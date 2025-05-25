import React, { useState } from 'react';
import '../../css/Chat.css';
import ChatRoom from '../../components/chat/ChatRoom';
import CreateGroupModal from './CreateGroupModal';
import ChatFriendList from '../../components/chat/ChatFriendList';
import ChatRoomList from '../../components/chat/ChatRoomList';

const Chat = () => {
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [friends, setFriends] = useState([]);
    const [selectedRoomId, setSelectedRoomId] = useState(null);

    return (
        <div className="chat-container">
            <div className="chat-main">
                {/* 친구 목록 */}
                <div className="chat-friend-list-panel">
                    <ChatFriendList />
                </div>

                {/* 채팅방 리스트 */}
                <ChatRoomList
                    selectedRoomId={selectedRoomId}
                    setSelectedRoomId={setSelectedRoomId}
                    onOpenCreateModal={() => setShowCreateModal(true)}
                    onSelectRoom={(roomId) => setSelectedRoomId(roomId)}
                />


                {/* 채팅방 본문 */}
                <ChatRoom
                    roomId={selectedRoomId}
                    onReadAllMessages={(roomId) => {
                        // ChatRoomList 내부에서 처리됨. 필요 시 상태 전달 가능.
                    }}
                />
            </div>

            {/* 그룹채팅 생성 모달 */}
            {showCreateModal && (
                <CreateGroupModal
                    friends={friends}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={() => setShowCreateModal(false)} // ChatRoomList에서 자동 반영되도록 구성
                />
            )}
        </div>
    );
};

export default Chat;
