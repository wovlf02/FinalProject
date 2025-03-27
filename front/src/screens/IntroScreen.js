import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Image } from 'react-native';

const IntroScreen = ({ navigation }) => {
    return (
        <View style={styles.container}>
            {/* 로고 및 앱 이름 */}
            <View style={styles.logoContainer}>
                <Image source={require('../assets/intro.png')} style={styles.logo} />
                <Text style={styles.appName}>StudyMate</Text>
            </View>

            {/* 간략한 설명 */}
            <Text style={styles.mainDescription}>학습 관리의 모든 것</Text>

            {/* 주요 기능 아이콘 */}
            <View style={styles.iconSection}>
                <View style={styles.iconCard}>
                    <Image source={require('../assets/community.png')} style={styles.icon} />
                    <Text style={styles.iconLabel}>커뮤니티</Text>
                </View>
                <View style={styles.iconCard}>
                    <Image source={require('../assets/personal.png')} style={styles.icon} />
                    <Text style={styles.iconLabel}>개인 학습 관리</Text>
                </View>
                <View style={styles.iconCard}>
                    <Image source={require('../assets/group.png')} style={styles.icon} />
                    <Text style={styles.iconLabel}>그룹 학습 관리</Text>
                </View>
            </View>

            {/* 버튼 섹션 */}
            <View style={styles.buttonContainer}>
                <TouchableOpacity
                    style={styles.loginButton}
                    onPress={() => navigation.navigate('Login')}
                >
                    <Text style={styles.buttonText}>로그인</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={styles.signUpButton}
                    onPress={() => navigation.navigate('Register')}
                >
                    <Text style={styles.buttonText}>회원가입</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#E3F2FD',
        alignItems: 'center',
        justifyContent: 'center',
    },
    logoContainer: {
        alignItems: 'center',
        marginBottom: 20,
    },
    logo: {
        width: 80,
        height: 80,
    },
    appName: {
        fontSize: 32,
        fontWeight: 'bold',
        color: '#007BFF',
        marginTop: 10,
    },
    mainDescription: {
        fontSize: 22,
        fontWeight: 'bold',
        color: '#007BFF',
        textAlign: 'center',
        marginBottom: 30,
    },
    subDescription: {
        fontSize: 16,
        color: '#666',
        textAlign: 'center',
        marginBottom: 30,
    },
    iconSection: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        width: '90%',
        marginBottom: 20,
    },
    iconCard: {
        alignItems: 'center',
    },
    icon: {
        width: 60,
        height: 60,
        marginBottom: 10,
    },
    iconLabel: {
        fontSize: 14,
        color: '#555',
    },
    buttonContainer: {
        flexDirection: 'row',
        width: '80%',
        justifyContent: 'space-between',
        marginTop: 30,
    },
    loginButton: {
        backgroundColor: '#007BFF',
        paddingVertical: 15,
        flex: 1,
        borderRadius: 25,
        marginRight: 10,
    },
    signUpButton: {
        backgroundColor: '#00C853',
        paddingVertical: 15,
        flex: 1,
        borderRadius: 25,
    },
    buttonText: {
        color: '#FFF',
        fontSize: 16,
        textAlign: 'center',
        fontWeight: 'bold',
    },
});

export default IntroScreen;
