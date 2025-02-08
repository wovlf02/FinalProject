import React, { useState, useEffect } from 'react';
import {View, Text, StyleSheet, TextInput, TouchableOpacity, Alert, Dimensions, ScrollView, Image} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { useNavigation } from '@react-navigation/native';
import api from "../../api/api";

const { width, height } = Dimensions.get('window');

const RegisterScreen = () => {
    const [username, setUsername] = useState('');
    const [isUsernameValid, setIsUsernameValid] = useState(null);
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [isPasswordMatch, setIsPasswordMatch] = useState(null);
    const [name, setName] = useState('');
    const [phone, setPhone] = useState('');
    const [email, setEmail] = useState('');
    const [emailDomain, setEmailDomain] = useState('');
    const [isCustomDomain, setIsCustomDomain] = useState(false);
    const [authInput, setAuthInput] = useState('');
    const [isAuthSent, setIsAuthSent] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0); // 타이머 (초 단위)
    const [isFormValid, setIsFormValid] = useState(false);

    const navigation = useNavigation(); // navigation 객체 가져오기

    // 아이디 중복 확인
    const handleUserIdCheck = async () => {
        try{
            const response = await api.post('/auth/check-username', {username});
            if(!response.data.success){
                setIsUsernameValid(true);
                Alert.alert('중복 확인', '사용 가능한 아이디입니다.');
            } else {
                setIsUsernameValid(false);
                Alert.alert('중복 확인', '이미 사용 중인 아이디입니다.');
            }
        } catch(error){
            Alert.alert('오류', '아이디 확인 중 문제가 발생했습니다.');
        }
    };

    // 비밀번호 입력 처리
    const handlePasswordChange = (text) => {
        setPassword(text);
        setIsPasswordMatch(text === passwordConfirm);
    };

    // 비밀번호 입력 내용 일치 여부 확인
    const handlePasswordConfirmChange = (text) => {
        setPasswordConfirm(text);
        setIsPasswordMatch(password === text);
    };

    // 전화번호 포맷팅
    const handlePhoneChange = (text) => {
        const formatted = text
            .replace(/[^0-9]/g, '') // 숫자 외의 문자 제거
            .replace(/^(\d{3})(\d{0,4})?(\d{0,4})?$/, (match, p1, p2, p3) => {
                if (p3) return `${p1}-${p2}-${p3}`; // 3-4-4 형식
                if (p2) return `${p1}-${p2}`; // 3-4 형식
                return p1; // 3 형식
            });
        setPhone(formatted);
    };

    // 인증번호 발송
    const sendEmailVerificationCode = async () => {
        if (email && emailDomain) {
            // 이메일 주소 조합
            const fullEmail = `${email}@${emailDomain}`;

            try {
                const response = await api.post('/auth/send-email-code', { email: fullEmail });

                if (!response.data.success) {
                    setIsAuthSent(true); // 인증번호 발송 상태 설정
                    setTimeLeft(300); // 타이머 300초 (5분) 설정
                    Alert.alert('인증번호 발송', response.data.message);
                } else {
                    Alert.alert('오류', response.data.message);
                }
            } catch (error) {
                Alert.alert('오류', '인증번호 발송 중 문제가 발생했습니다.');
            }
        } else {
            Alert.alert('오류', '이메일을 올바르게 입력해주세요.');
        }
    };


    // 이메일 도메인 선택 처리
    const handleDomainChange = (value) => {
        if(value === 'custom'){
            setIsCustomDomain(true);
            setEmailDomain('');
        } else {
            setIsCustomDomain(false);
            setEmailDomain(value);
        }
    };

    // 타이머 동작
    useEffect(() => {
        if (timeLeft > 0) {
            const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
            return () => clearTimeout(timer);
        }
    }, [timeLeft]);

    // 인증번호 확인
    const checkEmailVerificationCode = async () => {
        try {
            const fullEmail = `${email}@${emailDomain}`; // 이메일 주소 조합
            const response = await api.post('/auth/verify-email-code', {
                email: fullEmail,
                code: authInput,
            });

            if (!response.data.success) {
                Alert.alert('인증 성공', '인증이 완료되었습니다.');
            } else {
                Alert.alert('인증 실패', '인증번호가 일치하지 않습니다.');
            }
        } catch (error) {
            console.error('인증번호 확인 오류:', error);
            Alert.alert('오류', '인증번호 확인 중 문제가 발생했습니다.');
        }
    };


    // 회원가입 처리
    const handleRegister = async () => {
        const userData = {
            username,
            password,
            name,
            phone,
            email: `${email}@${emailDomain}`,
        };
        try{
            const response = await api.post('/auth/register', userData);
            if(!response.data.success){
                Alert.alert('회원가입 성공', response.data.message, [
                    {
                        text: '확인',
                        onPress: () => navigation.navigate('Login'),
                    },
                ]);
            } else {
                Alert.alert('회원가입 실패', response.data.message);
            }
        } catch(error){
            Alert.alert('오류', '회원가입 중 문제가 발생했습니다.')
        }
    };

    // 회원가입 버튼 활성화 여부 확인
    useEffect(() => {
        setIsFormValid(
            username &&
            isUsernameValid &&
            password &&
            passwordConfirm &&
            isPasswordMatch &&
            name &&
            phone &&
            email &&
            emailDomain &&
            authInput
        );
    }, [username, isUsernameValid, password, passwordConfirm, isPasswordMatch, name, phone, email, emailDomain, authInput]);

    return (
        <ScrollView contentContainerStyle={styles.container}>
            <Text style={styles.title}>회원가입</Text>

            <View style={styles.inputGroupRow}>
                {/* 아이디 입력 */}
                <TextInput
                    style={[
                        styles.inputSmall,
                        isUsernameValid === false ? styles.inputError : {},
                        isUsernameValid === true ? styles.inputSuccess : {},
                    ]}
                    placeholder="아이디"
                    value={username}
                    onChangeText={setUsername}
                />
                {/* 체크/엑스 표시 */}
                {isUsernameValid !== null && (
                    <Image
                        source={
                            isUsernameValid
                                ? require('../../assets/check.png')
                                : require('../../assets/x.png')
                        }
                        style={styles.idValidationIcon}
                    />
                )}
                {/* 중복확인 버튼 */}
                <TouchableOpacity style={styles.checkButtonInline} onPress={handleUserIdCheck}>
                    <Text style={styles.buttonText}>중복확인</Text>
                </TouchableOpacity>
            </View>


            {/* 비밀번호 입력 */}
            <TextInput
                style={styles.input}
                placeholder="비밀번호"
                secureTextEntry
                value={password}
                onChangeText={handlePasswordChange}
            />
            {/* 비밀번호 확인 */}
            <View style={styles.inputGroup}>
                <TextInput
                    style={[
                        styles.input,
                        isPasswordMatch === false ? styles.inputError : {},
                        isPasswordMatch === true ? styles.inputSuccess : {},
                    ]}
                    placeholder="비밀번호 재입력"
                    secureTextEntry
                    value={passwordConfirm}
                    onChangeText={handlePasswordConfirmChange}
                />
                {isPasswordMatch !== null && (
                    <Image
                        source={
                            isPasswordMatch
                                ? require('../../assets/check.png')
                                : require('../../assets/x.png')
                        }
                        style={styles.passwordValidationIcon}
                    />
                )}

            </View>

            {/* 이름 입력 */}
            <TextInput
                style={styles.input}
                placeholder="이름"
                value={name}
                onChangeText={setName}
            />

            {/* 전화번호 입력 */}
            <TextInput
                style={styles.input}
                placeholder="전화번호"
                keyboardType="number-pad"
                value={phone}
                onChangeText={handlePhoneChange}
            />

            {/* 이메일 입력 */}
            <View style={styles.emailContainer}>
                <TextInput
                    style={styles.emailInput}
                    placeholder="아이디"
                    value={email}
                    onChangeText={setEmail}
                />
                <Text style={styles.atSymbol}>@</Text>
                {isCustomDomain ? (
                    <TextInput
                        style={styles.emailInput}
                        placeholder=""
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

            {/* 인증번호 발송 */}
            <TouchableOpacity style={styles.authButton} onPress={sendEmailVerificationCode}>
                <Text style={styles.buttonText}>인증번호 발송</Text>
            </TouchableOpacity>

            {/* 인증번호 입력 및 타이머 */}
            {isAuthSent && (
                <View style={styles.inputGroupRow}>
                    {/* 인증번호 입력 칸 */}
                    <TextInput
                        style={styles.inputSmall}
                        placeholder="인증번호"
                        keyboardType="number-pad"
                        value={authInput}
                        onChangeText={setAuthInput}
                    />
                    {/* 타이머 */}
                    <Text style={styles.timerInline}>
                        {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                    </Text>
                    {/* 인증번호 확인 버튼 */}
                    <TouchableOpacity style={styles.checkButtonInline} onPress={checkEmailVerificationCode}>
                        <Text style={styles.buttonText}>확인</Text>
                    </TouchableOpacity>
                </View>
            )}


            {/* 회원가입 버튼 */}
            <TouchableOpacity
                style={[styles.submitButton, isFormValid ? styles.activeButton : styles.inactiveButton]}
                disabled={!isFormValid}
                onPress={handleRegister}
            >
                <Text style={styles.buttonText}>회원가입</Text>
            </TouchableOpacity>
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    container: {
        flexGrow: 1,
        backgroundColor: '#E3F2FD',
        alignItems: 'center',
        padding: width * 0.05,
    },
    title: {
        fontSize: height * 0.035,
        fontWeight: 'bold',
        color: '#007BFF',
        marginBottom: height * 0.03,
    },
    input: {
        width: '100%',
        height: height * 0.06,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.015,
        paddingHorizontal: width * 0.04,
        marginBottom: height * 0.02,
    },
    inputError: {
        borderColor: 'red',
        borderWidth: 2,
    },
    inputSuccess: {
        borderColor: 'green',
        borderWidth: 2,
    },
    inputGroup: {
        width: '100%',
        marginBottom: height * 0.03,
    },
    emailContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: height * 0.02,
        width: '100%',
    },
    idValidationIcon: {
        position: 'absolute',
        right: 118,
        top: 16,
        width: 24,
        height: 24,
    },
    passwordValidationIcon: {
        position: 'absolute',
        right: 17,
        top: 16,
        width: 24,
        height: 24,
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
        fontSize: height * 0.03,
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
        color: '#FFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
    timerInline: {
        position: 'absolute',
        right: width * 0.28, // 타이머가 입력 칸 바로 오른쪽에 위치
        top: height * 0.015, // 입력 칸 높이에 맞게 위치 조정
        fontSize: height * 0.02,
        color: '#FF0000', // 빨간색 타이머
    },
    checkButton: {
        marginTop: height * 0.02,
        backgroundColor: '#007BFF',
        paddingVertical: height * 0.015,
        paddingHorizontal: width * 0.05,
        borderRadius: height * 0.015,
        alignSelf: 'center',
    },
    submitButton: {
        width: '100%',
        height: height * 0.06,
        borderRadius: height * 0.015,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: height * 0.03,
    },
    activeButton: {
        backgroundColor: '#007BFF',
    },
    inactiveButton: {
        backgroundColor: '#CCC',
    },
    // 검증 아이콘의 기본 스타일
    validationIconId: {
        position: 'absolute', // 아이콘을 입력 칸 오른쪽에 고정
        right: width * 0.28, // 입력 칸 오른쪽 여백
        top: height * 0.013, // 입력 칸 위쪽 여백
        fontSize: height * 0.02, // 아이콘 크기
    },
    validationIconPassword: {
        position: 'absolute', // 아이콘을 입력 칸 오른쪽에 고정
        right: width * 0.03, // 입력 칸 오른쪽 여백
        top: height * 0.013, // 입력 칸 위쪽 여백
        fontSize: height * 0.02, // 아이콘 크기
    },
    validationIconEmailVerificationCode: {
        position: 'absolute', // 아이콘을 입력 칸 오른쪽에 고정
        right: width * 0.28, // 입력 칸 오른쪽 여백
        top: height * 0.013, // 입력 칸 위쪽 여백
        fontSize: height * 0.02, // 아이콘 크기
    },
    // 초록색 체크 아이콘 스타일
    iconSuccess: {
        color: 'green', // 초록색
    },
    // 빨간색 X 아이콘 스타일
    iconError: {
        color: 'red', // 빨간색
    },
    inputGroupRow: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: height * 0.03,
        width: '100%',
    },

    inputSmall: {
        flex: 3,
        height: height * 0.06,
        backgroundColor: '#FFFFFF',
        borderRadius: height * 0.015,
        paddingHorizontal: width * 0.04,
        marginRight: width * 0.02,
    },

    checkButtonInline: {
        flex: 1,
        height: height * 0.06,
        backgroundColor: '#007BFF',
        borderRadius: height * 0.015,
        justifyContent: 'center',
        alignItems: 'center',
        marginRight: width * 0.02,
    },

});

export default RegisterScreen;
