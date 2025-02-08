import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Alert, Dimensions } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import api from '../../api/api';

const { width, height } = Dimensions.get('window');

const FindAccountScreen = ({ navigation }) => {
    const [activeTab, setActiveTab] = useState('username'); // 현재 탭 상태 ('username' | 'password')
    const [emailId, setEmailId] = useState(''); // 이메일 아이디
    const [emailDomain, setEmailDomain] = useState(''); // 이메일 도메인
    const [isCustomDomain, setIsCustomDomain] = useState(false); // 직접 입력 도메인 여부
    const [authCode, setAuthCode] = useState('');
    const [isAuthSent, setIsAuthSent] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0);
    const [isAuthVerified, setIsAuthVerified] = useState(false);
    const [username, setUsername] = useState('');
    const [userId, setUserId] = useState('');

    const fullEmail = `${emailId}@${emailDomain}`; // 완전한 이메일 주소

    const startTimer = () => {
        setTimeLeft(300);
        const interval = setInterval(() => {
            setTimeLeft((prev) => {
                if (prev <= 1) clearInterval(interval);
                return prev - 1;
            });
        }, 1000);
    };

    const sendAuthCode = async () => {
        if (!emailId || !emailDomain) {
            Alert.alert('오류', '이메일을 올바르게 입력해주세요.');
            return;
        }
        try {
            const response = await api.post('/auth/send-email-code', { email: fullEmail });
            if (!response.data.success) {
                Alert.alert('인증번호 발송', '인증번호가 이메일로 전송되었습니다.');
                setIsAuthSent(true);
                startTimer();
            } else {
                Alert.alert('오류', response.data.message || '인증번호 발송에 실패했습니다.');
            }
        } catch (error) {
            Alert.alert('오류', '인증번호 발송 중 문제가 발생했습니다.');
        }
    };

    const verifyAuthCode = async () => {
        try {
            const response = await api.post('/auth/verify-email-code', { email: fullEmail, code: authCode });
            if (!response.data.success) {
                Alert.alert('인증 성공', '인증이 완료되었습니다.');
                setIsAuthVerified(true);
            } else {
                Alert.alert('인증 실패', '인증번호가 일치하지 않습니다.');
            }
        } catch (error) {
            Alert.alert('오류', '인증번호 확인 중 문제가 발생했습니다.');
        }
    };

    const handleFindUsername = async () => {
        try {
            const response = await api.post('/auth/find-username', { email: fullEmail });
            if (response.data.success) {
                setUsername(response.data.username);
                Alert.alert('아이디 찾기 성공', `회원님의 아이디는 "${response.data.username}"입니다.`, [
                    {
                        text: '확인',
                        onPress: () => {
                            navigation.navigate('Login');
                        },
                    },
                ]);
            } else {
                Alert.alert('아이디 찾기 실패', response.data.message || '아이디를 찾을 수 없습니다.');
            }
        } catch (error) {
            Alert.alert('오류', '아이디 찾기 중 문제가 발생했습니다.');
        }
    };

    const handleFindPassword = () => {
        if (isAuthVerified) {
            navigation.navigate('ResetPassword', { userId });
        } else {
            Alert.alert('오류', '이메일 인증을 완료해주세요.');
        }
    };

    const handleDomainChange = (value) => {
        if (value === 'custom') {
            setIsCustomDomain(true);
            setEmailDomain('');
        } else {
            setIsCustomDomain(false);
            setEmailDomain(value);
        }
    };

    return (
        <View style={styles.container}>
            {/* 상단 탭 */}
            <View style={styles.tabContainer}>
                <TouchableOpacity
                    style={[styles.tab, activeTab === 'username' && styles.activeTab]}
                    onPress={() => setActiveTab('username')}
                >
                    <Text style={[styles.tabText, activeTab === 'username' && styles.activeTabText]}>
                        아이디 찾기
                    </Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={[styles.tab, activeTab === 'password' && styles.activeTab]}
                    onPress={() => setActiveTab('password')}
                >
                    <Text style={[styles.tabText, activeTab === 'password' && styles.activeTabText]}>
                        비밀번호 찾기
                    </Text>
                </TouchableOpacity>
            </View>

            {/* 아이디 찾기 */}
            {activeTab === 'username' && (
                <View style={styles.contentContainer}>
                    <View style={styles.emailContainer}>
                        <TextInput
                            style={styles.emailInput}
                            placeholder="이메일 아이디"
                            value={emailId}
                            onChangeText={setEmailId}
                        />
                        <Text style={styles.atSymbol}>@</Text>
                        {isCustomDomain ? (
                            <TextInput
                                style={styles.emailInput}
                                placeholder="직접 입력"
                                value={emailDomain}
                                onChangeText={setEmailDomain}
                            />
                        ) : (
                            <View style={styles.pickerContainer}>
                                <Picker
                                    selectedValue={emailDomain}
                                    onValueChange={handleDomainChange}
                                    style={styles.picker}
                                >
                                    <Picker.Item label="도메인 선택" value="" />
                                    <Picker.Item label="gmail.com" value="gmail.com" />
                                    <Picker.Item label="naver.com" value="naver.com" />
                                    <Picker.Item label="daum.net" value="daum.net" />
                                    <Picker.Item label="직접 입력" value="custom" />
                                </Picker>
                            </View>
                        )}
                    </View>

                    <TouchableOpacity style={styles.authButton} onPress={sendAuthCode}>
                        <Text style={styles.buttonText}>인증번호 발송</Text>
                    </TouchableOpacity>

                    {/* 인증번호 입력 및 타이머, 인증 버튼 */}
                    {isAuthSent && (
                        <View style={styles.authContainer}>
                            <View style={styles.inputGroupRow}>
                                <TextInput
                                    style={styles.inputSmall}
                                    placeholder="인증번호"
                                    keyboardType="number-pad"
                                    value={authCode}
                                    onChangeText={setAuthCode}
                                />
                                <Text style={styles.timer}>
                                    {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                                </Text>
                                <TouchableOpacity style={styles.authButtonInline} onPress={verifyAuthCode}>
                                    <Text style={styles.buttonText}>확인</Text>
                                </TouchableOpacity>
                            </View>
                        </View>
                    )}

                    {isAuthVerified && (
                        <TouchableOpacity style={styles.submitButton} onPress={handleFindUsername}>
                            <Text style={styles.buttonText}>아이디 확인</Text>
                        </TouchableOpacity>
                    )}
                </View>
            )}

            {/* 비밀번호 찾기 */}
            {activeTab === 'password' && (
                <View style={styles.contentContainer}>
                    {/* 아이디 입력 */}
                    <TextInput
                        style={styles.input}
                        placeholder="아이디"
                        value={userId}
                        onChangeText={setUserId}
                    />

                    {/* 이메일 입력 */}
                    <View style={styles.emailContainer}>
                        <TextInput
                            style={styles.emailInput}
                            placeholder="이메일 아이디"
                            value={emailId}
                            onChangeText={setEmailId}
                        />
                        <Text style={styles.atSymbol}>@</Text>
                        {isCustomDomain ? (
                            <TextInput
                                style={styles.emailInput}
                                placeholder="직접 입력"
                                value={emailDomain}
                                onChangeText={setEmailDomain}
                            />
                        ) : (
                            <View style={styles.pickerContainer}>
                                <Picker
                                    selectedValue={emailDomain}
                                    onValueChange={handleDomainChange}
                                    style={styles.picker}
                                >
                                    <Picker.Item label="도메인 선택" value="" />
                                    <Picker.Item label="gmail.com" value="gmail.com" />
                                    <Picker.Item label="naver.com" value="naver.com" />
                                    <Picker.Item label="daum.net" value="daum.net" />
                                    <Picker.Item label="직접 입력" value="custom" />
                                </Picker>
                            </View>
                        )}
                    </View>

                    {/* 인증번호 발송 버튼 */}
                    <TouchableOpacity style={styles.authButton} onPress={sendAuthCode}>
                        <Text style={styles.buttonText}>인증번호 발송</Text>
                    </TouchableOpacity>

                    {/* 인증번호 입력 및 확인 */}
                    {isAuthSent && (
                        <View style={styles.authContainer}>
                            <View style={styles.inputGroupRow}>
                                <TextInput
                                    style={styles.inputSmall}
                                    placeholder="인증번호"
                                    keyboardType="number-pad"
                                    value={authCode}
                                    onChangeText={setAuthCode}
                                />
                                <Text style={styles.timer}>
                                    {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                                </Text>
                                <TouchableOpacity style={styles.authButtonInline} onPress={verifyAuthCode}>
                                    <Text style={styles.buttonText}>확인</Text>
                                </TouchableOpacity>
                            </View>
                        </View>
                    )}

                    {/* 비밀번호 재설정 버튼 */}
                    {isAuthVerified && userId && (
                        <TouchableOpacity style={styles.submitButton} onPress={handleFindPassword}>
                            <Text style={styles.buttonText}>비밀번호 재설정</Text>
                        </TouchableOpacity>
                    )}
                </View>
            )}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#E3F2FD',
        padding: 20,
    },
    tabContainer: {
        position: 'absolute', // 절대 위치
        bottom: 0, // 화면 하단
        left: 0,
        right: 0,
        flexDirection: 'row', // 가로 정렬
        justifyContent: 'space-between', // 탭 간 간격 균등
        alignItems: 'center', // 수직 정렬
        paddingVertical: 10, // 세로 패딩
        paddingHorizontal: 20, // 좌우 패딩
        borderTopWidth: 0, // 경계선 제거
        backgroundColor: 'transparent', // 배경 투명
    },
    tab: {
        flex: 1,
        padding: 15,
        alignItems: 'center',
        backgroundColor: '#FFFFFF',
        borderRadius: 10,
        marginHorizontal: 5,
    },
    activeTab: {
        backgroundColor: '#007BFF',
    },
    tabText: {
        fontSize: 16,
        color: '#007BFF',
    },
    activeTabText: {
        color: '#FFFFFF',
        fontWeight: 'bold',
    },
    contentContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        width: '100%',
    },
    emailContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 15,
        width: '100%',
    },
    emailInput: {
        flex: 3,
        height: height * 0.06,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.015,
        paddingHorizontal: width * 0.04,
        marginRight: width * 0.02,
    },
    atSymbol: {
        fontSize: 20,
        marginHorizontal: width * 0.02,
    },
    pickerContainer: {
        flex: 4,
        height: height * 0.06,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.015,
        justifyContent: 'center',
    },
    picker: {
        width: '100%',
        height: '100%',
    },
    authContainer: {
        width: '100%',
        marginBottom: height * 0.03,
    },
    inputGroupRow: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        width: '100%',
    },
    inputSmall: {
        flex: 2,
        height: height * 0.06,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.015,
        paddingHorizontal: width * 0.04,
        marginRight: width * 0.02,
    },
    timer: {
        flex: 1,
        textAlign: 'center',
        color: '#FF0000',
        fontWeight: 'bold',
        fontSize: height * 0.02,
    },
    authButtonInline: {
        flex: 1.5,
        height: height * 0.06,
        backgroundColor: '#007BFF',
        borderRadius: height * 0.015,
        justifyContent: 'center',
        alignItems: 'center',
    },
    authButton: {
        width: '100%',
        height: height * 0.06,
        backgroundColor: '#007BFF',
        borderRadius: height * 0.015,
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: height * 0.03,
    },
    buttonText: {
        color: '#FFFFFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
    submitButton: {
        width: '100%',
        height: height * 0.06,
        backgroundColor: '#007BFF',
        borderRadius: height * 0.015,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 15,
    },
    input: {
        width: '100%',
        height: height * 0.06,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.015,
        paddingHorizontal: width * 0.04,
        marginBottom: height * 0.02,
    },
});

export default FindAccountScreen;
