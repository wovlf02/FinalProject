import React, { useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    TextInput,
    TouchableOpacity,
    Image,
    Alert,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import api from '../../api/api';
import EncryptedStorage from 'react-native-encrypted-storage';

const platformIcons = {
    google: require('../../assets/google.png'),
    kakao: require('../../assets/kakao.png'),
    naver: require('../../assets/naver.png'),
    github: require('../../assets/github.png'),
};

const LoginScreen = ({ navigation }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordVisible, setPasswordVisible] = useState(false);

    const handleLogin = async () => {
        try {
            const dummyUser = {
                username: 'testuser',
                email: 'test@example.com',
                name: '홍길동',
                accessToken: 'dummy-access-token',
                refreshToken: 'dummy-refresh-token',
            };

            await EncryptedStorage.setItem('refreshToken', dummyUser.refreshToken);

            navigation.replace('Main', {
                ...dummyUser,
            });
        } catch (error) {
            console.error(error);
            Alert.alert('로그인 오류', '처리 중 문제가 발생했습니다.');
        }
    };

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
        <LinearGradient colors={['#E3F2FD', '#FFFFFF']} style={styles.container}>
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
                {Object.keys(platformIcons).map((platform) => (
                    <TouchableOpacity key={platform} onPress={() => handleSocialLogin(platform)}>
                        <Image source={platformIcons[platform]} style={styles.socialIcon} />
                    </TouchableOpacity>
                ))}
            </View>

            {/* 하단 메뉴 */}
            <View style={styles.footer}>
                <TouchableOpacity onPress={() => navigation.navigate('FindAccount')}>
                    <Text style={styles.footerText}>계정 찾기</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                    <Text style={styles.footerText}>회원가입</Text>
                </TouchableOpacity>
            </View>
        </LinearGradient>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    title: {
        fontSize: 32,
        fontWeight: 'bold',
        color: '#007BFF',
        marginBottom: 40,
    },
    input: {
        width: '80%',
        height: 52,
        backgroundColor: '#FFFFFF',
        borderRadius: 26,
        paddingHorizontal: 20,
        marginBottom: 15,
        elevation: 3,
    },
    passwordContainer: {
        width: '80%',
        height: 52,
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
        borderRadius: 26,
        paddingHorizontal: 20,
        marginBottom: 20,
        elevation: 3,
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
        height: 52,
        backgroundColor: '#007BFF',
        borderRadius: 26,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 5,
        elevation: 4,
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
        marginBottom: 25,
    },
    socialIcon: {
        width: 48,
        height: 48,
        marginHorizontal: 12,
    },
    footer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '80%',
    },
    footerText: {
        fontSize: 15,
        color: '#007BFF',
        textDecorationLine: 'underline',
    },
});

export default LoginScreen;
