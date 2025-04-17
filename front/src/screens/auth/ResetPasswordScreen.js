import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert, StyleSheet, Dimensions, Image } from 'react-native';
import api from '../../api/api';

const { width, height } = Dimensions.get('window');

const ResetPasswordScreen = ({ route, navigation }) => {
    const { userId } = route.params; // 이전 화면에서 전달된 userId
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [passwordStrength, setPasswordStrength] = useState(0); // 비밀번호 강도 (0-100)

    // 비밀번호 강도 계산 함수
    const calculatePasswordStrength = (password) => {
        let strength = 0;
        if (password.length >= 8) strength += 25; // 최소 길이
        if (/[A-Z]/.test(password)) strength += 25; // 대문자 포함
        if (/[a-z]/.test(password)) strength += 25; // 소문자 포함
        if (/[0-9]/.test(password) || /[^a-zA-Z0-9]/.test(password)) strength += 25; // 숫자 또는 특수문자 포함
        return Math.min(strength, 100);
    };

    // 비밀번호 입력 핸들러
    const handlePasswordChange = (value) => {
        setPassword(value);
        setPasswordStrength(calculatePasswordStrength(value));
    };

    // 비밀번호 확인 핸들러
    const handlePasswordConfirmChange = (value) => {
        setPasswordConfirm(value);
    };

    // 비밀번호 일치 여부 확인
    const isPasswordMatching = password === passwordConfirm && passwordConfirm.length > 0;

    // 비밀번호 재설정 API 호출
    const handleResetPassword = async () => {
        if (!isPasswordMatching) {
            Alert.alert('오류', '비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            const response = await api.post('/auth/reset-password', {
                username: userId,
                newPassword: password
            });
            if (response.data.success) {
                Alert.alert('성공', response.data.message, [
                    { text: '확인', onPress: () => navigation.navigate('Login') },
                ]);
            } else {
                Alert.alert('오류', response.data.message || '비밀번호 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error(error);
            Alert.alert('오류', '비밀번호 변경 중 문제가 발생했습니다.');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>비밀번호 재설정</Text>

            {/* 비밀번호 입력 */}
            <TextInput
                style={styles.input}
                placeholder="새 비밀번호"
                secureTextEntry
                value={password}
                onChangeText={handlePasswordChange}
            />

            {/* 비밀번호 강도 게이지 */}
            <View style={styles.strengthContainer}>
                <View
                    style={[
                        styles.strengthBar,
                        { width: `${passwordStrength}%`, backgroundColor: getStrengthColor(passwordStrength) },
                    ]}
                />
            </View>
            <Text style={styles.strengthText}>
                {passwordStrength < 50
                    ? '약함'
                    : passwordStrength < 75
                        ? '보통'
                        : passwordStrength < 100
                            ? '강함'
                            : '매우 강함'}
            </Text>

            {/* 비밀번호 재입력 */}
            <View style={styles.inputContainer}>
                <TextInput
                    style={styles.inputWithIcon}
                    placeholder="비밀번호 확인"
                    secureTextEntry
                    value={passwordConfirm}
                    onChangeText={handlePasswordConfirmChange}
                />
                {/* 일치 여부 아이콘 */}
                {passwordConfirm.length > 0 && (
                    <Image
                        source={isPasswordMatching ? require('../../assets/check.png') : require('../../assets/x.png')}
                        style={styles.icon}
                    />
                )}
            </View>

            {/* 비밀번호 재설정 버튼 */}
            <TouchableOpacity style={styles.submitButton} onPress={handleResetPassword}>
                <Text style={styles.buttonText}>비밀번호 재설정</Text>
            </TouchableOpacity>
        </View>
    );
};

// 비밀번호 강도에 따라 색상 반환
const getStrengthColor = (strength) => {
    if (strength < 50) return '#FF4D4D'; // 약함
    if (strength < 75) return '#FFA500'; // 보통
    if (strength < 100) return '#4CAF50'; // 강함
    return '#007BFF'; // 매우 강함
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#E3F2FD',
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#007BFF',
        marginBottom: 20,
    },
    input: {
        width: '100%',
        height: 50,
        backgroundColor: '#FFFFFF',
        borderRadius: 10,
        paddingHorizontal: 15,
        marginBottom: 20,
    },
    inputContainer: {
        position: 'relative',
        width: '100%',
        marginBottom: 20,
    },
    inputWithIcon: {
        width: '100%',
        height: 50,
        backgroundColor: '#FFFFFF',
        borderRadius: 10,
        paddingHorizontal: 15,
        paddingRight: 40, // 아이콘 공간 확보
    },
    icon: {
        position: 'absolute',
        right: 10,
        top: 13, // 아이콘을 TextInput 중앙에 맞춤
        width: 24,
        height: 24,
    },
    strengthContainer: {
        width: '100%',
        height: 10,
        backgroundColor: '#D9D9D9',
        borderRadius: 5,
        overflow: 'hidden',
        marginBottom: 10,
    },
    strengthBar: {
        height: '100%',
        borderRadius: 5,
    },
    strengthText: {
        alignSelf: 'flex-start',
        fontSize: 14,
        color: '#555',
        marginBottom: 20,
    },
    submitButton: {
        width: '100%',
        height: 50,
        backgroundColor: '#007BFF',
        borderRadius: 10,
        justifyContent: 'center',
        alignItems: 'center',
    },
    buttonText: {
        color: '#FFFFFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
});

export default ResetPasswordScreen;
