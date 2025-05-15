// ChatRoomListScreen.js
import React, { useEffect, useState, useRef } from 'react';
import {
    View, Text, StyleSheet, FlatList, TextInput, TouchableOpacity, Alert, Image
} from 'react-native';
import FastImage from 'react-native-fast-image';
import { useNavigation } from '@react-navigation/native';
import EncryptedStorage from 'react-native-encrypted-storage';
import { jwtDecode } from 'jwt-decode';
import moment from 'moment';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import api from '../../api/api';

const BASE_URL = 'http://192.168.78.59:8080';

const ChatRoomListScreen = () => {
    const navigation = useNavigation();
    const [chatRooms, setChatRooms] = useState([]);
    const [userId, setUserId] = useState(null);
    const [filter, setFilter] = useState('ALL'); // ALL, GROUP, DIRECT
    const [searchQuery, setSearchQuery] = useState('');
    const stompClient = useRef(null);

    useEffect(() => {
        const init = async () => {
            const token = await EncryptedStorage.getItem('accessToken');
            if (token) {
                const decoded = jwtDecode(token);
                const id = Number(decoded.sub);
                setUserId(id);
                fetchChatRooms(id);
                connectWebSocket(id);
            }
        };
        init();

        return () => {
            if (stompClient.current && stompClient.current.connected) {
                stompClient.current.disconnect(() => console.log('Disconnected'));
            }
        };
    }, []);

    const fetchChatRooms = async (uid) => {
        try {
            const res = await api.get(`/chat/rooms/user/${uid}`);
            setChatRooms(res.data);
        } catch (err) {
            console.error('채팅방 목록 오류:', err);
        }
    };

    const connectWebSocket = (uid) => {
        const socket = new SockJS(`${BASE_URL}/ws`);
        stompClient.current = Stomp.over(socket);

        stompClient.current.connect({}, () => {
            stompClient.current.subscribe(`/sub/chat/user/${uid}`, () => {
                fetchChatRooms(uid); // 새 메시지 수신 시 목록 갱신
            });
        });
    };

    const handleLongPress = (roomId) => {
        Alert.alert('채팅방 옵션', '원하는 작업을 선택하세요.', [
            {
                text: '상단 고정',
                onPress: () => pinRoom(roomId),
            },
            {
                text: '채팅방 나가기',
                onPress: () => exitRoom(roomId),
                style: 'destructive',
            },
            { text: '취소', style: 'cancel' },
        ]);
    };

    const pinRoom = (roomId) => {
        setChatRooms(prev =>
            prev.map(room =>
                room.roomId === roomId ? { ...room, pinned: !room.pinned } : room
            )
        );
    };

    const exitRoom = async (roomId) => {
        try {
            await api.delete(`/chat/rooms/${roomId}/exit`, {
                data: { userId },
            });
            Alert.alert('알림', '채팅방에서 나갔습니다.');
            fetchChatRooms(userId);
        } catch {
            Alert.alert('오류', '채팅방 나가기 실패');
        }
    };

    const filteredRooms = chatRooms
        .filter(room => {
            if (filter === 'GROUP') return room.roomType === 'GROUP';
            if (filter === 'DIRECT') return room.roomType === 'DIRECT';
            return true;
        })
        .filter(room => room.roomName.toLowerCase().includes(searchQuery.toLowerCase()))
        .sort((a, b) => (b.pinned ? 1 : 0) - (a.pinned ? 1 : 0)); // 상단 고정 우선 정렬

    const renderItem = ({ item }) => (
        <TouchableOpacity
            style={styles.chatRoomCard}
            onPress={() => navigation.navigate('ChatRoom', { roomId: item.roomId })}
            onLongPress={() => handleLongPress(item.roomId)}
        >
            <FastImage
                source={{ uri: BASE_URL + item.profileImageUrl }}
                style={styles.profileImage}
            />
            <View style={styles.chatInfo}>
                <Text style={styles.chatRoomName}>
                    {item.roomName} {item.pinned && '📌'}
                </Text>
                <Text style={styles.latestMessage} numberOfLines={1}>
                    {item.lastMessage || '(아직 메시지 없음)'}
                </Text>
            </View>
            <View style={styles.rightInfo}>
                {item.lastMessageAt && (
                    <Text style={styles.metaText}>
                        {moment(item.lastMessageAt).format('HH:mm')}
                    </Text>
                )}
                <Text style={styles.metaText}>👥 {item.participantCount}</Text>
                {item.unreadCount > 0 && (
                    <View style={styles.unreadBadge}>
                        <Text style={styles.unreadText}>{item.unreadCount}</Text>
                    </View>
                )}
                <TouchableOpacity>
                    <Image
                        source={require('../../assets/bell_off.png')}
                        style={{ width: 18, height: 18, marginTop: 4 }}
                    />
                </TouchableOpacity>
            </View>
        </TouchableOpacity>
    );

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.headerTitle}>채팅</Text>
                <TextInput
                    placeholder="채팅방 검색"
                    placeholderTextColor="#A08C7B"
                    value={searchQuery}
                    onChangeText={setSearchQuery}
                    style={styles.searchInput}
                />
            </View>

            <View style={styles.filterRow}>
                {['ALL', 'GROUP', 'DIRECT'].map(type => (
                    <TouchableOpacity key={type} onPress={() => setFilter(type)}>
                        <Text style={[styles.filterTab, filter === type && styles.activeTab]}>
                            {type === 'ALL' ? '전체' : type === 'GROUP' ? '그룹' : '1:1'}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            <FlatList
                data={filteredRooms}
                keyExtractor={item => item.roomId.toString()}
                renderItem={renderItem}
                contentContainerStyle={{ paddingBottom: 100 }}
            />

            <TouchableOpacity
                style={styles.floatingButton}
                onPress={() => navigation.navigate('CreateChatRoom')}
            >
                <Text style={styles.plusText}>＋</Text>
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F4F1EC',
    },
    header: {
        paddingTop: 20,
        paddingBottom: 8,
        backgroundColor: '#F4F1EC',
        paddingHorizontal: 20,
    },
    headerTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#382F2D',
        marginBottom: 8,
    },
    searchInput: {
        backgroundColor: '#FFFDF9',
        borderRadius: 12,
        padding: 10,
        fontSize: 14,
        color: '#3C3C3C',
        borderWidth: 1,
        borderColor: '#E0D7D2',
    },
    filterRow: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        paddingVertical: 10,
        backgroundColor: '#FFFDF9',
        borderBottomWidth: 1,
        borderColor: '#E5DCD7',
    },
    filterTab: {
        fontSize: 14,
        color: '#A08C7B',
        paddingVertical: 6,
        paddingHorizontal: 16,
        borderRadius: 20,
    },
    activeTab: {
        backgroundColor: '#E8D7C8',
        color: '#382F2D',
        fontWeight: 'bold',
    },
    chatRoomCard: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#FFFDF9',
        padding: 14,
        marginHorizontal: 16,
        marginBottom: 12,
        borderRadius: 12,
        elevation: 2,
    },
    profileImage: {
        width: 50,
        height: 50,
        borderRadius: 25,
        marginRight: 14,
    },
    chatInfo: {
        flex: 1,
    },
    chatRoomName: {
        fontWeight: 'bold',
        fontSize: 16,
        color: '#382F2D',
    },
    latestMessage: {
        fontSize: 14,
        color: '#7A6E66',
        marginTop: 4,
    },
    rightInfo: {
        alignItems: 'flex-end',
        justifyContent: 'center',
        gap: 4,
    },
    metaText: {
        fontSize: 12,
        color: '#A08C7B',
    },
    unreadBadge: {
        backgroundColor: '#FF3B30',
        minWidth: 22,
        height: 22,
        borderRadius: 11,
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: 6,
        marginTop: 2,
    },
    unreadText: {
        color: '#FFF',
        fontWeight: 'bold',
        fontSize: 12,
    },
    floatingButton: {
        position: 'absolute',
        right: 20,
        bottom: 20,
        backgroundColor: '#A3775C',
        width: 60,
        height: 60,
        borderRadius: 30,
        justifyContent: 'center',
        alignItems: 'center',
        elevation: 10,
    },
    plusText: {
        fontSize: 30,
        color: '#FFF',
        marginBottom: 2,
    },
});

export default ChatRoomListScreen;
