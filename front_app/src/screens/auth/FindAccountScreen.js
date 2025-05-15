import React, { useState } from 'react';
import {
    View, Text, TextInput, TouchableOpacity, StyleSheet, Dimensions, Alert,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import api from '../../api/api';

const { width, height } = Dimensions.get('window');

const FindAccountScreen = ({ navigation }) => {
    const [activeTab, setActiveTab] = useState('username');
    const [emailId, setEmailId] = useState('');
    const [emailDomain, setEmailDomain] = useState('');
    const [isCustomDomain, setIsCustomDomain] = useState(false);
    const [authCode, setAuthCode] = useState('');
    const [isAuthSent, setIsAuthSent] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0);
    const [isAuthVerified, setIsAuthVerified] = useState(false);
    const [username, setUsername] = useState('');

    const fullEmail = `${emailId}@${emailDomain}`;

    const startTimer = () => {
        setTimeLeft(300);
        const timer = setInterval(() => {
            setTimeLeft(prev => {
                if (prev <= 1) {
                    clearInterval(timer);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    };

    const sendAuthCode = async () => {
        if (!emailId || !emailDomain) {
            return Alert.alert('입력 오류', '이메일을 정확히 입력해주세요.');
        }

        try {
            if (activeTab === 'username') {
                await api.post('/auth/find-username/send-code', { email: fullEmail });
            } else {
                if (!username.trim()) return Alert.alert('입력 오류', '아이디를 입력해주세요.');
                await api.post('/auth/password/request', { username, email: fullEmail });
            }
            setIsAuthSent(true);
            setIsAuthVerified(false);
            startTimer();
            Alert.alert('성공', '인증번호가 발송되었습니다.');
        } catch (e) {
            Alert.alert('오류', e?.response?.data?.message || '인증번호 발송 중 오류가 발생했습니다.');
        }
    };

    const verifyAuthCode = async () => {
        try {
            const endpoint = activeTab === 'username'
                ? '/auth/find-username/verify-code'
                : '/auth/password/verify-code';

            const res = await api.post(endpoint, { email: fullEmail, code: authCode });

            if (activeTab === 'username') {
                const foundUsername = res.data.data;
                Alert.alert('아이디 찾기 성공', `회원님의 아이디는 "${foundUsername}"입니다.`, [
                    { text: '로그인하기', onPress: () => navigation.navigate('Login') },
                ]);
            } else {
                const success = res.data.data;
                if (success) {
                    setIsAuthVerified(true);
                    Alert.alert('성공', '이메일 인증이 완료되었습니다.');
                } else {
                    Alert.alert('실패', '인증번호가 일치하지 않습니다.');
                }
            }
        } catch (e) {
            Alert.alert('오류', e?.response?.data?.message || '인증 확인 중 오류가 발생했습니다.');
        }
    };

    const handleFindPassword = () => {
        if (!isAuthVerified) {
            return Alert.alert('실패', '이메일 인증을 먼저 완료해주세요.');
        }
        navigation.navigate('ResetPassword', { username });
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>계정 찾기</Text>

            <View style={styles.tabRow}>
                <TouchableOpacity
                    style={[styles.tab, activeTab === 'username' && styles.tabActive]}
                    onPress={() => {
                        setActiveTab('username');
                        setIsAuthSent(false);
                        setIsAuthVerified(false);
                    }}>
                    <Text style={[styles.tabText, activeTab === 'username' && styles.tabTextActive]}>
                        아이디 찾기
                    </Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={[styles.tab, activeTab === 'password' && styles.tabActive]}
                    onPress={() => {
                        setActiveTab('password');
                        setIsAuthSent(false);
                        setIsAuthVerified(false);
                    }}>
                    <Text style={[styles.tabText, activeTab === 'password' && styles.tabTextActive]}>
                        비밀번호 찾기
                    </Text>
                </TouchableOpacity>
            </View>

            {activeTab === 'password' && (
                <TextInput
                    style={styles.input}
                    placeholder="아이디"
                    value={username}
                    onChangeText={setUsername}
                />
            )}

            <View style={styles.emailRow}>
                <TextInput
                    style={styles.emailInput}
                    placeholder="이메일"
                    value={emailId}
                    onChangeText={setEmailId}
                />
                <Text style={styles.at}>@</Text>
                {isCustomDomain ? (
                    <TextInput
                        style={styles.emailInput}
                        placeholder="직접 입력"
                        value={emailDomain}
                        onChangeText={setEmailDomain}
                    />
                ) : (
                    <Picker
                        selectedValue={emailDomain}
                        onValueChange={(val) => {
                            setIsCustomDomain(val === 'custom');
                            setEmailDomain(val === 'custom' ? '' : val);
                        }}
                        style={styles.picker}>
                        <Picker.Item label="선택" value="" />
                        <Picker.Item label="gmail.com" value="gmail.com" />
                        <Picker.Item label="naver.com" value="naver.com" />
                        <Picker.Item label="daum.net" value="daum.net" />
                        <Picker.Item label="직접 입력" value="custom" />
                    </Picker>
                )}
            </View>

            <TouchableOpacity style={styles.button} onPress={sendAuthCode}>
                <Text style={styles.buttonText}>인증번호 발송</Text>
            </TouchableOpacity>

            {isAuthSent && (
                <>
                    <TextInput
                        style={styles.input}
                        placeholder="인증번호"
                        keyboardType="numeric"
                        value={authCode}
                        onChangeText={setAuthCode}
                    />
                    <View style={styles.rowBetween}>
                        <Text style={styles.timer}>
                            {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                        </Text>
                        <TouchableOpacity style={styles.smallButton} onPress={verifyAuthCode}>
                            <Text style={styles.buttonText}>확인</Text>
                        </TouchableOpacity>
                    </View>
                </>
            )}

            {activeTab === 'password' && isAuthVerified && (
                <TouchableOpacity style={styles.button} onPress={handleFindPassword}>
                    <Text style={styles.buttonText}>비밀번호 재설정</Text>
                </TouchableOpacity>
            )}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F6FAFE',
        alignItems: 'center',
    },
    title: {
        fontSize: height * 0.035,
        fontWeight: 'bold',
        color: '#000',
        marginTop: height * 0.1,
        marginBottom: height * 0.04,
    },
    tabRow: {
        flexDirection: 'row',
        width: width * 0.85,
        marginBottom: height * 0.03,
        justifyContent: 'space-between',
    },
    tab: {
        flex: 1,
        height: height * 0.065,
        marginHorizontal: width * 0.01,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.032,
        justifyContent: 'center',
        alignItems: 'center',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
        elevation: 2,
    },
    tabActive: {
        backgroundColor: '#000000',
    },
    tabText: {
        color: '#000',
        fontSize: 16,
    },
    tabTextActive: {
        color: '#fff',
        fontWeight: 'bold',
    },
    input: {
        width: width * 0.85,
        height: height * 0.06,
        backgroundColor: '#fff',
        borderRadius: 30,
        paddingHorizontal: 20,
        marginBottom: height * 0.015,
    },
    emailRow: {
        flexDirection: 'row',
        alignItems: 'center',
        width: width * 0.85,
        marginVertical: height * 0.01,
    },
    emailInput: {
        flex: 3,
        height: height * 0.06,
        backgroundColor: '#fff',
        borderRadius: 30,
        paddingHorizontal: 15,
    },
    at: {
        marginHorizontal: 6,
        fontSize: 18,
    },
    picker: {
        flex: 3,
        height: height * 0.06,
        backgroundColor: '#fff',
        borderRadius: 30,
    },
    button: {
        width: width * 0.85,
        height: height * 0.065,
        backgroundColor: '#000',
        borderRadius: height * 0.032,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: height * 0.02,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: 'bold',
    },
    verifyRow: {
        flexDirection: 'row',
        alignItems: 'center',
        width: width * 0.85,
        marginTop: height * 0.015,
    },
    timer: {
        marginLeft: 8,
        fontSize: 14,
        fontWeight: 'bold',
        color: 'red',
    },
    rowBetween: {
        width: width * 0.85,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: height * 0.01,
    },
    smallButton: {
        backgroundColor: '#000',
        paddingHorizontal: 20,
        paddingVertical: 10,
        borderRadius: 30,
    },
});

export default FindAccountScreen;
