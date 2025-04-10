import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const HomeScreen = ({ route }) => {
    // 🔹 로그인 후 전달받은 사용자 정보
    const { username, email, name } = route.params || {};

    return (
        <View style={styles.container}>
            <Text style={styles.title}>홈 화면</Text>
            <Text style={styles.infoText}>아이디: {username}</Text>
            <Text style={styles.infoText}>이메일: {email}</Text>
            <Text style={styles.infoText}>이름: {name}</Text>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#E3F2FD',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    title: {
        fontSize: 28,
        fontWeight: 'bold',
        color: '#007BFF',
        marginBottom: 20,
    },
    infoText: {
        fontSize: 18,
        color: '#333',
        marginBottom: 10,
    },
});

export default HomeScreen;
