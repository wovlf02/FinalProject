import React, {useEffect, useState} from 'react';

function BackendTest() {
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetch('http://localhost:8080/api/hello')
            .then(res => res.json())
            .then(data => setMessage(data.message))
            .catch(err => {
                console.error('API 호출 실패:', err);
                setMessage('API 호출 실패');
            });
    }, []);

    return (
        <div>
            <h2>백엔드 연동 테스트 페이지</h2>
            <p>서버 메시지: {message}</p>
        </div>
    );
}

export default BackendTest;
