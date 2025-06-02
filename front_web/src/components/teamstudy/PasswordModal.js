import React, { useEffect, useRef, useState } from 'react';
import '../../css/Modal.css';

const PasswordModal = ({ roomTitle, onSubmit, onCancel }) => {
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const inputRef = useRef(null);

    useEffect(() => {
        inputRef.current?.focus();
    }, []);

    const handleSubmit = () => {
        if (!password.trim()) {
            setError('비밀번호를 입력해주세요.');
            return;
        }
        setError('');
        onSubmit(password);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSubmit();
        }
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>🔒 {roomTitle} 입장</h2>
                <p>이 방은 비밀번호가 필요합니다.</p>

                <label htmlFor="room-password" className="visually-hidden">비밀번호 입력</label>
                <input
                    id="room-password"
                    type="password"
                    ref={inputRef}
                    placeholder="비밀번호 입력"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onKeyDown={handleKeyDown}
                    aria-label="비밀번호 입력"
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
