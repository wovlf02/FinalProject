// src/hooks/useTeamRoomSocket.js
import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const SOCKET_URL = `${process.env.REACT_APP_API_BASE}/ws`;

const useTeamRoomSocket = (roomId, onChatReceived) => {
    const [messages, setMessages] = useState([]);
    const stompClientRef = useRef(null);

    const connectSocket = () => {
        const socket = new SockJS(SOCKET_URL);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('✅ WebSocket 연결됨');
                stompClient.subscribe(`/sub/team/${roomId}/chat`, (message) => {
                    const payload = JSON.parse(message.body);
                    setMessages(prev => [...prev, payload]);
                    onChatReceived?.(payload);
                });

                // 기타 이벤트도 여기서 구독 가능 (예: 입장 알림, 발표 시작 등)
                stompClient.subscribe(`/sub/team/${roomId}/event`, (message) => {
                    const payload = JSON.parse(message.body);
                    console.log('[이벤트 수신]', payload);
                    // 필요한 콜백 실행
                });
            },
            onDisconnect: () => {
                console.log('❌ WebSocket 연결 해제됨');
            },
        });

        stompClient.activate();
        stompClientRef.current = stompClient;
    };

    const disconnectSocket = () => {
        if (stompClientRef.current) {
            stompClientRef.current.deactivate();
            console.log('🧹 WebSocket 연결 종료');
        }
    };

    const sendChat = (message, sender) => {
        if (!stompClientRef.current || !stompClientRef.current.connected) return;

        const payload = {
            type: 'CHAT',
            roomId,
            sender,
            content: message,
            timestamp: new Date().toISOString(),
        };

        stompClientRef.current.publish({
            destination: `/pub/team/${roomId}/chat`,
            body: JSON.stringify(payload),
        });
    };

    const sendEvent = (type, data = {}) => {
        if (!stompClientRef.current || !stompClientRef.current.connected) return;

        const payload = {
            type,
            roomId,
            ...data,
        };

        stompClientRef.current.publish({
            destination: `/pub/team/${roomId}/event`,
            body: JSON.stringify(payload),
        });
    };

    // cleanup
    useEffect(() => {
        return () => {
            disconnectSocket();
        };
    }, []);

    return {
        connectSocket,
        disconnectSocket,
        sendChat,
        sendEvent,
        messages,
    };
};

export default useTeamRoomSocket;
