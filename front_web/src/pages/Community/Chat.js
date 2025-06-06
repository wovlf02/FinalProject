import React, { useState } from 'react';
import '../../css/Chat.css';
import ChatRoom from '../../components/chat/ChatRoom';
import ChatRoomList from '../../components/chat/ChatRoomList';
import CreateGroupModal from './CreateGroupModal';

const Chat = () => {
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [selectedRoomId, setSelectedRoomId] = useState(null);

    return (
        <div className="chat-container">
            <div className="chat-main">
                {/* 왼쪽: 채팅방 목록 */}
                <div className="chat-room-list-panel">
                    <ChatRoomList
                        selectedRoomId={selectedRoomId}
                        setSelectedRoomId={setSelectedRoomId}
                        onOpenCreateModal={() => setShowCreateModal(true)}
                        onSelectRoom={setSelectedRoomId}
                    />
                </div>
                {/* 오른쪽: 채팅방 본문 */}
                <div className="chat-room-panel">
                    <ChatRoom
                        roomId={selectedRoomId}
                        onReadAllMessages={() => {}}
                    />
                </div>
            </div>
            {/* 그룹채팅 생성 모달 */}
            {showCreateModal && (
                <CreateGroupModal
                    onClose={() => setShowCreateModal(false)}
                    onCreate={() => setShowCreateModal(false)}
                />
            )}
        </div>
    );
};

export default Chat;
