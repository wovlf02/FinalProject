// src/hooks/useTeamRoomSocket.js
import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const SOCKET_URL = `${process.env.REACT_APP_API_BASE}/ws`;

const useTeamRoomSocket = (roomId, onChatReceived, onEventReceived) => {
    const [messages, setMessages] = useState([]);
    const stompClientRef = useRef(null);

    const connectSocket = () => {
        if (stompClientRef.current?.connected) return;

        const socket = new SockJS(SOCKET_URL, null, {
            transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
            withCredentials: true // ✅ 세션 쿠키 전달
        });
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            debug: (str) => console.log('[STOMP]', str),
            onConnect: () => {
                console.log('✅ WebSocket 연결됨');

                stompClient.subscribe(`/sub/team/${roomId}/chat`, (message) => {
                    const payload = JSON.parse(message.body);
                    setMessages((prev) => [...prev, payload]);
                    onChatReceived?.(payload);
                });

                stompClient.subscribe(`/sub/team/${roomId}/event`, (message) => {
                    const payload = JSON.parse(message.body);
                    console.log('[이벤트 수신]', payload);
                    onEventReceived?.(payload);
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
            stompClientRef.current = null;
        }
    };

    const sendChat = (message, sender) => {
        if (!stompClientRef.current?.connected) return;

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
        if (!stompClientRef.current?.connected) return;

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

    // roomId 바뀌면 자동 연결
    useEffect(() => {
        connectSocket();

        return () => {
            disconnectSocket();
        };
    }, [roomId]);

    return {
        connectSocket,
        disconnectSocket,
        sendChat,
        sendEvent,
        messages,
    };
};

export default useTeamRoomSocket;
