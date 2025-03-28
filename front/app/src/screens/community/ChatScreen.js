import React, { useState } from 'react';
import { View, Text, StyleSheet, FlatList, Image, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const ChatScreen = () => {
    const navigation = useNavigation();

    // 🔹 하드코딩된 채팅방 데이터
    const chatRooms = [
        {
            id: '1',
            name: '개발자 스터디 그룹',
            latestMessage: '프론트엔드 개발 어떻게 진행할까요?',
            unreadCount: 2,
            profileImage: require('../../assets/sample1.png')
        },
        {
            id: '2',
            name: 'React Native 팀',
            latestMessage: '다음 회의는 언제 하시나요?',
            unreadCount: 0,
            profileImage: require('../../assets/sample2.png')
        },
        {
            id: '3',
            name: '백엔드 개발자 모임',
            latestMessage: 'Spring Boot API 연결 완료했습니다!',
            unreadCount: 5,
            profileImage: require('../../assets/sample3.png')
        }
    ];

    // 🔹 채팅방 렌더링 함수
    const renderChatRoom = ({ item }) => (
        <TouchableOpacity
            style={styles.chatRoomCard}
            onPress={() => navigation.navigate('ChatRoom', { chatId: item.id })}
        >
            <Image source={item.profileImage} style={styles.profileImage} />
            <View style={styles.chatInfo}>
                <Text style={styles.chatRoomName}>{item.name}</Text>
                <Text style={styles.latestMessage} numberOfLines={1}>{item.latestMessage}</Text>
            </View>
            {item.unreadCount > 0 && (
                <View style={styles.unreadBadge}>
                    <Text style={styles.unreadText}>{item.unreadCount}</Text>
                </View>
            )}
        </TouchableOpacity>
    );

    return (
        <View style={styles.container}>
            {/* 📜 채팅방 리스트 */}
            <FlatList
                data={chatRooms}
                keyExtractor={(item) => item.id}
                renderItem={renderChatRoom}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F8F9FA',
    },
    chatRoomCard: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#FFF',
        padding: 15,
        marginVertical: 5,
        marginHorizontal: 15,
        borderRadius: 10,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 3,
    },
    profileImage: {
        width: 50,
        height: 50,
        borderRadius: 25,
        marginRight: 15,
    },
    chatInfo: {
        flex: 1,
    },
    chatRoomName: {
        fontWeight: 'bold',
        fontSize: 16,
    },
    latestMessage: {
        fontSize: 14,
        color: '#666',
        marginTop: 3,
    },
    unreadBadge: {
        backgroundColor: '#FF3B30',
        width: 24,
        height: 24,
        borderRadius: 12,
        alignItems: 'center',
        justifyContent: 'center',
    },
    unreadText: {
        color: '#FFF',
        fontWeight: 'bold',
    },
});

export default ChatScreen;