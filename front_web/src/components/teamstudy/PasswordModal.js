// src/components/teamstudy/PasswordModal.js
import React, { useState } from 'react';
import '../../css/Modal.css';

const PasswordModal = ({ roomTitle, onSubmit, onCancel }) => {
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = () => {
        if (!password.trim()) {
            setError('비밀번호를 입력해주세요.');
            return;
        }
        setError('');
        onSubmit(password);
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>🔒 {roomTitle} 입장</h2>
                <p>이 방은 비밀번호가 필요합니다.</p>

                <input
                    type="password"
                    placeholder="비밀번호 입력"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                {error && <p className="error-message">{error}</p>}

                <div className="modal-buttons">
                    <button onClick={handleSubmit}>확인</button>
                    <button onClick={onCancel}>취소</button>
                </div>
            </div>
        </div>
    );
};

export default PasswordModal;
