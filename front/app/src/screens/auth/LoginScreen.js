import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Image, Alert } from 'react-native';
import api from '../../api/api'; // 서버 API 호출 파일 import
import EncryptedStorage from 'react-native-encrypted-storage';

const LoginScreen = ({ navigation }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordVisible, setPasswordVisible] = useState(false);

    // 일반 로그인 처리
    const handleLogin = async () => {
        try {
            const response = await api.post('/auth/login', { username, password });

            if (response.status === 200) {
                const { accessToken, refreshToken, username, email, name } = response.data;

                // 🔒 보안 저장소에 Refresh Token 저장
                await EncryptedStorage.setItem('refreshToken', refreshToken);

                // 🔄 홈 화면으로 이동하며 사용자 데이터 전달
                navigation.navigate('Home', { username, email, name, accessToken });
            }
        } catch (error) {
            console.error(error);
            Alert.alert('로그인 실패', '아이디 또는 비밀번호를 확인하세요.');
        }
    };

    // 소셜 로그인 처리
    const handleSocialLogin = async (platform) => {
        try {
            const response = await api.get(`/auth/${platform}`);
            if (response.status === 200) {
                const { redirectUrl } = response.data;
                navigation.navigate('WebView', { redirectUrl, platform });
            }
        } catch (error) {
            console.error(error);
            Alert.alert('소셜 로그인 실패', '다시 시도해주세요.');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>로그인</Text>

            {/* 일반 로그인 */}
            <TextInput
                style={styles.input}
                placeholder="아이디"
                placeholderTextColor="#999"
                value={username}
                onChangeText={setUsername}
            />
            <View style={styles.passwordContainer}>
                <TextInput
                    style={styles.passwordInput}
                    placeholder="비밀번호"
                    placeholderTextColor="#999"
                    secureTextEntry={!passwordVisible}
                    value={password}
                    onChangeText={setPassword}
                />
                <TouchableOpacity onPress={() => setPasswordVisible(!passwordVisible)}>
                    <Image
                        source={
                            passwordVisible
                                ? require('../../assets/password-show.png')
                                : require('../../assets/password-hide.png')
                        }
                        style={styles.eyeIcon}
                    />
                </TouchableOpacity>
            </View>
            <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
                <Text style={styles.loginButtonText}>로그인</Text>
            </TouchableOpacity>

            {/* 소셜 로그인 */}
            <View style={styles.socialLoginContainer}>
                <TouchableOpacity onPress={() => handleSocialLogin('google')}>
                    <Image source={require('../../assets/google.png')} style={styles.socialIcon} />
                </TouchableOpacity>
                <TouchableOpacity onPress={() => handleSocialLogin('kakao')}>
                    <Image source={require('../../assets/kakao.png')} style={styles.socialIcon} />
                </TouchableOpacity>
                <TouchableOpacity onPress={() => handleSocialLogin('naver')}>
                    <Image source={require('../../assets/naver.png')} style={styles.socialIcon} />
                </TouchableOpacity>
                <TouchableOpacity onPress={() => handleSocialLogin('github')}>
                    <Image source={require('../../assets/github.png')} style={styles.socialIcon} />
                </TouchableOpacity>
            </View>

            {/* 계정 찾기 및 회원가입 */}
            <View style={styles.footer}>
                <TouchableOpacity onPress={() => navigation.navigate('FindAccount')}>
                    <Text style={styles.footerText}>계정 찾기</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                    <Text style={styles.footerText}>회원가입</Text>
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
    title: {
        fontSize: 28,
        fontWeight: 'bold',
        color: '#007BFF',
        marginBottom: 30,
    },
    input: {
        width: '80%',
        height: 50,
        backgroundColor: '#FFFFFF',
        borderRadius: 25,
        paddingHorizontal: 20,
        marginBottom: 15,
    },
    passwordContainer: {
        width: '80%',
        height: 50,
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
        borderRadius: 25,
        paddingHorizontal: 20,
        marginBottom: 15,
    },
    passwordInput: {
        flex: 1,
    },
    eyeIcon: {
        width: 24,
        height: 24,
    },
    loginButton: {
        width: '80%',
        height: 50,
        backgroundColor: '#007BFF',
        borderRadius: 25,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 15,
    },
    loginButtonText: {
        color: '#FFFFFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
    socialLoginContainer: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginTop: 30,
        marginBottom: 20,
    },
    socialIcon: {
        width: 50,
        height: 50,
        marginHorizontal: 10,
    },
    footer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '80%',
        marginTop: 20,
    },
    footerText: {
        fontSize: 14,
        color: '#007BFF',
        textDecorationLine: 'underline',
    },
});

export default LoginScreen;
