import React, { useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    TextInput,
    TouchableOpacity,
    Image,
    Alert,
    Dimensions,
} from 'react-native';
import EncryptedStorage from 'react-native-encrypted-storage';
import api from '../../api/api';
import { jwtDecode } from 'jwt-decode';

const { width, height } = Dimensions.get('window');

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
            const response = await api.post('/auth/login', { username, password });

            if (response.status === 200) {
                const { accessToken, refreshToken } = response.data.data;

                const decoded = jwtDecode(accessToken);
                const userId = decoded.sub;

                await EncryptedStorage.setItem('accessToken', accessToken);
                await EncryptedStorage.setItem('refreshToken', refreshToken);
                await EncryptedStorage.setItem('userId', String(userId));

                navigation.replace('Main');
            }
        } catch (error) {
            console.error(error);
            Alert.alert('로그인 실패', '아이디 또는 비밀번호를 확인하세요.');
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
        <View style={styles.container}>
            <View style={styles.contentWrapper}>
                <Text style={styles.title}>로그인</Text>

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

                <View style={styles.socialLoginContainer}>
                    {Object.keys(platformIcons).map((platform) => (
                        <TouchableOpacity key={platform} onPress={() => handleSocialLogin(platform)}>
                            <Image source={platformIcons[platform]} style={styles.socialIcon} />
                        </TouchableOpacity>
                    ))}
                </View>

                <View style={styles.footer}>
                    <TouchableOpacity onPress={() => navigation.navigate('FindAccount')}>
                        <Text style={styles.footerText}>계정 찾기</Text>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                        <Text style={styles.footerText}>회원가입</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F6FAFE',
    },
    contentWrapper: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        paddingTop: height * 0.05,
        paddingBottom: height * 0.05,
    },
    title: {
        fontSize: 32,
        fontWeight: 'bold',
        color: '#000',
        marginBottom: 40,
    },
    input: {
        width: width * 0.8,
        height: 52,
        backgroundColor: '#FFF',
        borderRadius: 30,
        paddingHorizontal: 20,
        marginBottom: 15,
        elevation: 3,
    },
    passwordContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        width: width * 0.8,
        height: 52,
        backgroundColor: '#FFF',
        borderRadius: 30,
        paddingHorizontal: 20,
        elevation: 3,
        marginBottom: 20,
    },
    passwordInput: {
        flex: 1,
    },
    eyeIcon: {
        width: 24,
        height: 24,
    },
    loginButton: {
        width: width * 0.8,
        height: 52,
        backgroundColor: '#000',
        borderRadius: 30,
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: 30,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.15,
        shadowRadius: 4,
        elevation: 4,
    },
    loginButtonText: {
        color: '#FFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
    socialLoginContainer: {
        flexDirection: 'row',
        justifyContent: 'center',
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
        width: width * 0.8,
        marginTop: 15,
    },
    footerText: {
        fontSize: 15,
        color: '#000',
        textDecorationLine: 'underline',
        fontWeight: '500',
    },
});

export default LoginScreen;
