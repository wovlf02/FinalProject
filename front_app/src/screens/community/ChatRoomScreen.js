import React, { useState, useEffect, useRef } from 'react';
import {
    View, Text, StyleSheet, TextInput, TouchableOpacity,
    FlatList, Image, KeyboardAvoidingView, Platform
} from 'react-native';
import moment from 'moment';
import FastImage from 'react-native-fast-image';
import { useNavigation, useRoute } from '@react-navigation/native';
import SockJS from 'sockjs-client';
import { CompatClient, Stomp } from '@stomp/stompjs';
import axios from 'axios';
import DocumentPicker from 'react-native-document-picker';
import EncryptedStorage from 'react-native-encrypted-storage';
import {jwtDecode} from 'jwt-decode';
import api from '../../api/api';

const BASE_URL = 'http://192.168.78.59:8080'; // Spring 서버 주소

const ChatRoomScreen = () => {
    const navigation = useNavigation();
    const { roomId, otherUserNickname } = useRoute().params;

    const [messages, setMessages] = useState([]);
    const [messageInput, setMessageInput] = useState('');
    const flatListRef = useRef(null);
    const stompClient = useRef(null);
    const [userId, setUserId] = useState(null);
    const [nickname, setNickname] = useState('');
    const [profileUrl, setProfileUrl] = useState('');

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const token = await EncryptedStorage.getItem('accessToken');
                if (!token) return;

                const decoded = jwtDecode(token);
                const userIdFromToken = Number(decoded.sub); // JWT에서 userId 추출
                setUserId(userIdFromToken);

                const res = await api.get(`/users/${userIdFromToken}`);
                const { nickname, profileImageUrl } = res.data;

                setNickname(nickname);
                setProfileUrl(profileImageUrl);
            } catch (err) {
                console.error('유저 정보 로드 실패:', err);
            }
        };

        fetchUserProfile();
    }, []);

    useEffect(() => {
        connectStomp();

        return () => {
            if (stompClient.current && stompClient.current.connected) {
                stompClient.current.disconnect(() => console.log('🔌 Disconnected'));
            }
        };
    }, []);

    const connectStomp = () => {
        const socket = new SockJS(`${BASE_URL}/ws`);
        stompClient.current = Stomp.over(socket);

        stompClient.current.connect({}, () => {
            console.log('✅ Connected to STOMP');

            stompClient.current.subscribe(`/sub/chat/room/${roomId}`, (message) => {
                const body = JSON.parse(message.body);
                setMessages(prev => [...prev, body]);
            });

            // 입장 메시지 전송 (옵션)
            stompClient.current.send(
                '/pub/chat/enter',
                {},
                JSON.stringify({ roomId, senderId: userId })
            );
        }, (error) => {
            console.log('❌ STOMP error:', error);
        });
    };

    const handleFilePickAndUpload = async () => {
        try {
            const res = await DocumentPicker.pickSingle({
                type: [DocumentPicker.types.images, DocumentPicker.types.pdf, DocumentPicker.types.plainText],
            });

            const formData = new FormData();
            formData.append('file', {
                uri: res.uri,
                type: res.type,
                name: res.name,
            });

            const uploadRes = await axios.post(`${BASE_URL}/chat/upload`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            const fileUrl = uploadRes.data.fileUrl;

            // 업로드된 파일을 메시지로 전송
            const fileMessage = {
                roomId,
                senderId: userId,
                nickname,
                profileUrl,
                content: fileUrl,
                type: 'FILE', // 또는 'IMAGE'
                time: new Date().toISOString()
            };

            stompClient.current?.send('/pub/chat/message', {}, JSON.stringify(fileMessage));
        } catch (err) {
            if (!DocumentPicker.isCancel(err)) {
                console.warn('파일 선택 또는 업로드 실패:', err);
            }
        }
    };

    const sendMessage = () => {
        if (!messageInput.trim()) return;

        const messagePayload = {
            roomId,
            senderId: userId,
            nickname,
            profileUrl,
            content: messageInput,
            type: 'TEXT',
            time: new Date().toISOString()
        };

        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.send('/pub/chat/message', {}, JSON.stringify(messagePayload));
            setMessageInput('');
        }
    };

    const renderMessage = ({ item }) => {
        const isMine = item.senderId === userId;

        if (isMine) {
            // 내가 보낸 메시지
            return (
                <View style={[styles.messageRow, { justifyContent: 'flex-end' }]}>
                    <View style={styles.metadataLeft}>
                        <Text style={styles.unread}>{item.unreadCount > 0 ? `안읽음 ${item.unreadCount}` : ''}</Text>
                        <Text style={styles.time}>{moment(item.time).format('HH:mm')}</Text>
                    </View>
                    <View style={[styles.messageBubble, styles.myBubble]}>
                        <Text style={styles.messageText}>{item.content}</Text>
                    </View>
                </View>
            );
        } else {
            // 상대방이 보낸 메시지
            return (
                <View style={styles.messageRow}>
                    <FastImage source={{ uri: BASE_URL + item.profileUrl }} style={styles.profileImage} />
                    <View style={{ flexShrink: 1 }}>
                        <Text style={styles.nickname}>{item.nickname}</Text>
                        <View style={styles.row}>
                            <View style={styles.messageBubble}>
                                <Text style={styles.messageText}>{item.content}</Text>
                            </View>
                            <View style={styles.metadataRight}>
                                <Text style={styles.unread}>{item.unreadCount > 0 ? `안읽음 ${item.unreadCount}` : ''}</Text>
                                <Text style={styles.time}>{moment(item.time).format('HH:mm')}</Text>
                            </View>
                        </View>
                    </View>
                </View>
            );
        }
    };


    return (
        <KeyboardAvoidingView style={styles.container} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
            {/* 상단 헤더 */}
            <View style={styles.header}>
                <TouchableOpacity onPress={() => navigation.goBack()}>
                    <Image source={require('../../assets/back.png')} style={styles.backIcon} />
                </TouchableOpacity>
                <Text style={styles.title}>{otherUserNickname}</Text>
                <TouchableOpacity>
                    <Image source={require('../../assets/search.png')} style={styles.searchIcon} />
                </TouchableOpacity>
            </View>

            {/* 채팅 메시지 목록 */}
            <FlatList
                ref={flatListRef}
                data={messages}
                keyExtractor={(item, index) => index.toString()}
                renderItem={renderMessage}
                contentContainerStyle={styles.messageList}
                onContentSizeChange={() => flatListRef.current?.scrollToEnd({ animated: true })}
            />

            {/* 입력창 영역 */}
            <View style={styles.inputBar}>
                <TouchableOpacity style={styles.plusButton} onPress={handleFilePickAndUpload}>
                    <Text style={styles.plusText}>＋</Text>
                </TouchableOpacity>
                <TextInput
                    value={messageInput}
                    onChangeText={setMessageInput}
                    placeholder="메시지를 입력하세요"
                    style={styles.input}
                />
                <TouchableOpacity onPress={sendMessage} style={styles.sendButton}>
                    <Text style={styles.sendText}>➤</Text>
                </TouchableOpacity>
            </View>
        </KeyboardAvoidingView>
    );
};

export default ChatRoomScreen;



const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F4F1EC',
    },
    header: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: 16,
        paddingVertical: 12,
        backgroundColor: '#FFFDF9',
        borderBottomWidth: 1,
        borderColor: '#EDE3DA',
    },
    backIcon: {
        width: 24,
        height: 24,
        marginRight: 12,
    },
    title: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#4E3F36',
        flex: 1,
    },
    searchIcon: {
        width: 22,
        height: 22,
    },
    messageList: {
        padding: 16,
        paddingBottom: 80,
    },
    messageContainer: {
        flexDirection: 'row',
        marginBottom: 14,
        alignItems: 'flex-end',
    },
    myMessage: {
        alignSelf: 'flex-end',
    },
    otherMessage: {
        alignSelf: 'flex-start',
    },
    messageHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 4,
    },
    inputBar: {
        position: 'absolute',
        bottom: 0,
        left: 0,
        right: 0,
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#FDF9F4',
        borderTopWidth: 1,
        borderColor: '#EDE3DA',
        paddingHorizontal: 12,
        paddingVertical: 10,
    },
    plusButton: {
        padding: 6,
        marginRight: 6,
    },
    plusText: {
        fontSize: 24,
        color: '#A3775C',
    },
    input: {
        flex: 1,
        height: 40,
        borderWidth: 1,
        borderColor: '#D7CCC8',
        borderRadius: 20,
        paddingHorizontal: 14,
        backgroundColor: '#FFFFFF',
        fontSize: 15,
        color: '#3C3C3C',
    },
    sendButton: {
        paddingLeft: 10,
    },
    sendText: {
        fontSize: 22,
        color: '#A3775C',
        fontWeight: 'bold',
    },
    messageRow: {
        flexDirection: 'row',
        alignItems: 'flex-end',
        marginBottom: 14,
        paddingHorizontal: 12,
    },

    row: {
        flexDirection: 'row',
        alignItems: 'flex-end',
        flexWrap: 'nowrap',
    },

    nickname: {
        fontSize: 13,
        fontWeight: 'bold',
        color: '#7B665A',
        marginBottom: 4,
        marginLeft: 8,
    },

    profileImage: {
        width: 36,
        height: 36,
        borderRadius: 18,
        marginRight: 8,
        borderWidth: 1,
        borderColor: '#E6DAD1',
    },

    messageBubble: {
        backgroundColor: '#FFFDF9',
        borderRadius: 16,
        borderWidth: 1,
        borderColor: '#E3D4C8',
        paddingVertical: 8,
        paddingHorizontal: 12,
        maxWidth: 260,
    },

    myBubble: {
        backgroundColor: '#E7DCD3',
        alignSelf: 'flex-end',
    },

    messageText: {
        fontSize: 15,
        color: '#3C3C3C',
    },

    metadataRight: {
        marginLeft: 8,
        alignItems: 'flex-end',
        justifyContent: 'flex-end',
        paddingBottom: 4,
    },

    metadataLeft: {
        marginRight: 8,
        alignItems: 'flex-end',
        justifyContent: 'flex-end',
        paddingBottom: 4,
    },

    unread: {
        fontSize: 11,
        color: '#A08C7B',
        marginBottom: 2,
    },

    time: {
        fontSize: 12,
        color: '#9A8E84',
    },

});
