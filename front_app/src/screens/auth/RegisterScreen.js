import React, { useState, useEffect } from 'react';
import {
    View, Text, TextInput, TouchableOpacity, StyleSheet,
    ScrollView, Image, Alert, Dimensions
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { launchImageLibrary } from 'react-native-image-picker';
import api from '../../api/api';
import { useNavigation } from '@react-navigation/native';

const { width, height } = Dimensions.get('window');

const habits = ['새벽형', '야행성', '주말 집중형', '루틴형'];
const allSubjects = ['국어', '수학', '영어', '과학', '사회', '기타'];

const RegisterScreen = () => {
    const navigation = useNavigation();
    const [step, setStep] = useState(1);

    const [username, setUsername] = useState('');
    const [isUsernameValid, setIsUsernameValid] = useState(null);
    const [nickname, setNickname] = useState('');
    const [isNicknameValid, setIsNicknameValid] = useState(null);
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [email, setEmail] = useState('');
    const [emailDomain, setEmailDomain] = useState('');
    const [isCustomDomain, setIsCustomDomain] = useState(false);
    const [authCode, setAuthCode] = useState('');
    const [isAuthSent, setIsAuthSent] = useState(false);
    const [authVerified, setAuthVerified] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0);

    const [isFormValid, setIsFormValid] = useState(false);
    const [grade, setGrade] = useState(null);
    const [subjects, setSubjects] = useState([]);
    const [studyHabit, setStudyHabit] = useState('');
    const [profileImage, setProfileImage] = useState(null);

    useEffect(() => {
        if (timeLeft > 0 && !authVerified) {
            const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
            return () => clearTimeout(timer);
        }
    }, [timeLeft, authVerified]);

    useEffect(() => {
        const valid =
            username && isUsernameValid &&
            nickname && isNicknameValid &&
            password && passwordConfirm && password === passwordConfirm &&
            email && emailDomain && authCode && authVerified &&
            grade && subjects.length > 0 && studyHabit;
        setIsFormValid(valid);
    }, [username, isUsernameValid, nickname, isNicknameValid, password, passwordConfirm, email, emailDomain, authCode, authVerified, grade, subjects, studyHabit]);

    const handleUsernameCheck = async () => {
        try {
            const res = await api.post('/auth/check-username', { username });
            setIsUsernameValid(res.data.data);
            Alert.alert('알림', res.data.data ? '사용 가능한 아이디입니다.' : '이미 사용 중입니다.');
        } catch {
            Alert.alert('오류', '아이디 확인 실패');
        }
    };

    const handleNicknameCheck = async () => {
        try {
            const res = await api.post('/auth/check-nickname', { nickname });
            setIsNicknameValid(res.data.data);
            Alert.alert('알림', res.data.data ? '사용 가능한 닉네임입니다.' : '이미 사용 중입니다.');
        } catch {
            Alert.alert('오류', '닉네임 확인 실패');
        }
    };

    const handleSendCode = async () => {
        try {
            await api.post('/auth/send-code', {
                email: `${email}@${emailDomain}`,
                type: 'REGISTER'  // ✅ 필수 필드 추가 (예: REGISTER, RESET 등)
            });
            setIsAuthSent(true);
            setAuthVerified(false);
            setTimeLeft(180);
            Alert.alert('알림', '인증번호가 발송되었습니다.');
        } catch {
            Alert.alert('오류', '인증번호 발송 실패');
        }
    };


    const handleVerifyCode = async () => {
        try {
            const res = await api.post('/auth/verify-code', {
                email: `${email}@${emailDomain}`,
                code: authCode
            });
            setAuthVerified(res.data.data);
            Alert.alert('알림', res.data.data ? '인증 성공' : '인증 실패');
        } catch {
            Alert.alert('오류', '인증번호 확인 실패');
        }
    };

    const toggleSubject = (subject) => {
        setSubjects(prev =>
            prev.includes(subject) ? prev.filter(s => s !== subject) : [...prev, subject]
        );
    };

    const handleImagePick = () => {
        launchImageLibrary({ mediaType: 'photo' }, (res) => {
            if (!res.didCancel && res.assets?.length > 0) {
                setProfileImage(res.assets[0]);
            }
        });
    };

    const handleRegister = async () => {
        const formData = new FormData();
        formData.append('username', username);
        formData.append('password', password);
        formData.append('email', `${email}@${emailDomain}`);
        formData.append('nickname', nickname);
        formData.append('grade', grade);
        subjects.forEach(subj => formData.append('subjects', subj));
        formData.append('studyHabit', studyHabit);
        if (profileImage) {
            formData.append('profileImage', {
                uri: profileImage.uri,
                name: profileImage.fileName || 'profile.jpg',
                type: profileImage.type || 'image/jpeg'
            });
        }

        try {
            const res = await api.post('/auth/register', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            if (res.data.success) {
                Alert.alert(
                    '회원가입 완료',
                    '로그인 화면으로 이동합니다.',
                    [{ text: '확인', onPress: () => navigation.navigate('Login') }]
                );
            } else {
                Alert.alert('오류', res.data.message || '회원가입에 실패했습니다.');
            }
        } catch {
            Alert.alert('오류', '회원가입 중 문제가 발생했습니다.');
        }
    };


    const getInputStyleWithValidation = (status) => {
        if (status === null) return [styles.inputWithButton, { borderColor: 'transparent', borderWidth: 1 }];
        if (status === true) return [styles.inputWithButton, { borderColor: 'green', borderWidth: 1 }];
        return [styles.inputWithButton, { borderColor: 'red', borderWidth: 1 }];
    };

    const getConfirmStyle = () => {
        if (passwordConfirm.length === 0) return [styles.inputWithButton, { borderColor: 'transparent', borderWidth: 1 }];
        return [styles.inputWithButton, {
            borderColor: password === passwordConfirm ? 'green' : 'red',
            borderWidth: 1
        }];
    };

    return (
        <ScrollView contentContainerStyle={styles.container}>
            <Text style={styles.title}>회원가입</Text>

            {step === 1 ? (
                <>
                    {/* 아이디 */}
                    <View style={styles.inputWrapper}>
                        <TextInput
                            style={getInputStyleWithValidation(isUsernameValid)}
                            placeholder="아이디"
                            value={username}
                            onChangeText={text => {
                                setUsername(text);
                                setIsUsernameValid(null);
                            }}
                        />
                        <TouchableOpacity style={styles.inlineButton} onPress={handleUsernameCheck}>
                            <Text style={styles.inlineButtonText}>중복확인</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 닉네임 */}
                    <View style={styles.inputWrapper}>
                        <TextInput
                            style={getInputStyleWithValidation(isNicknameValid)}
                            placeholder="닉네임"
                            value={nickname}
                            onChangeText={text => {
                                setNickname(text);
                                setIsNicknameValid(null);
                            }}
                        />
                        <TouchableOpacity style={styles.inlineButton} onPress={handleNicknameCheck}>
                            <Text style={styles.inlineButtonText}>중복확인</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 비밀번호 */}
                    <TextInput
                        style={styles.input}
                        placeholder="비밀번호"
                        secureTextEntry
                        value={password}
                        onChangeText={text => {
                            setPassword(text);
                        }}
                    />

                    {/* 비밀번호 확인 */}
                    <View style={styles.inputWrapper}>
                        <TextInput
                            style={getConfirmStyle()}
                            placeholder="비밀번호 확인"
                            secureTextEntry
                            value={passwordConfirm}
                            onChangeText={setPasswordConfirm}
                        />
                    </View>

                    {/* 이메일 */}
                    <View style={styles.emailRow}>
                        <TextInput
                            style={styles.emailInput}
                            placeholder="이메일 아이디"
                            value={email}
                            onChangeText={setEmail}
                        />
                        <Text style={styles.atSymbol}>@</Text>
                        {isCustomDomain ? (
                            <TextInput
                                style={styles.emailInput}
                                placeholder="도메인 입력"
                                value={emailDomain}
                                onChangeText={setEmailDomain}
                            />
                        ) : (
                            <View style={styles.emailDomainPickerWrapper}>
                                <Picker
                                    selectedValue={emailDomain}
                                    onValueChange={(val) => {
                                        setIsCustomDomain(val === 'custom');
                                        setEmailDomain(val === 'custom' ? '' : val);
                                    }}
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

                    {/* 인증 */}
                    <TouchableOpacity style={styles.authButton} onPress={handleSendCode}>
                        <Text style={styles.buttonText}>인증번호 발송</Text>
                    </TouchableOpacity>

                    {isAuthSent && (
                        <View style={styles.inputWrapper}>
                            <TextInput
                                style={styles.inputWithButton}
                                placeholder="인증번호 입력"
                                value={authCode}
                                onChangeText={setAuthCode}
                                keyboardType="number-pad"
                            />
                            {timeLeft > 0 && !authVerified && (
                                <Text style={styles.timerText}>
                                    {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                                </Text>
                            )}
                            <TouchableOpacity style={styles.inlineButton} onPress={handleVerifyCode}>
                                <Text style={styles.inlineButtonText}>확인</Text>
                            </TouchableOpacity>
                        </View>
                    )}

                    <TouchableOpacity style={styles.nextButton} onPress={() => setStep(2)}>
                        <Text style={styles.buttonText}>다음</Text>
                    </TouchableOpacity>
                </>
            ) : (
                <>
                    <Text style={styles.sectionTitle}>학년</Text>
                    <View style={styles.row}>
                        {[1, 2, 3].map(g => (
                            <TouchableOpacity
                                key={g}
                                style={[styles.gradeButton, grade === g && styles.selected]}
                                onPress={() => setGrade(g)}
                            >
                                <Text style={{ color: grade === g ? '#fff' : '#000' }}>{g}학년</Text>
                            </TouchableOpacity>
                        ))}
                    </View>

                    <Text style={styles.sectionTitle}>과목 선택</Text>
                    <View style={styles.rowWrap}>
                        {allSubjects.map(subj => (
                            <TouchableOpacity
                                key={subj}
                                style={[styles.gradeButton, subjects.includes(subj) && styles.selected]}
                                onPress={() => toggleSubject(subj)}
                            >
                                <Text style={{ color: subjects.includes(subj) ? '#fff' : '#000' }}>{subj}</Text>
                            </TouchableOpacity>
                        ))}
                    </View>

                    <Text style={styles.sectionTitle}>공부 습관</Text>
                    <View style={styles.studyHabitPickerWrapper}>
                        <Picker
                            selectedValue={studyHabit}
                            onValueChange={setStudyHabit}
                            style={styles.picker}
                        >
                            <Picker.Item label="선택" value="" />
                            {habits.map(h => <Picker.Item key={h} label={h} value={h} />)}
                        </Picker>
                    </View>

                    <Text style={styles.sectionTitle}>프로필 이미지</Text>
                    <TouchableOpacity onPress={handleImagePick}>
                        <Image
                            source={profileImage ? { uri: profileImage.uri } : require('../../assets/base_profile.png')}
                            style={styles.profileImage}
                        />
                    </TouchableOpacity>

                    <TouchableOpacity
                        style={[styles.submitButton, !isFormValid && styles.inactiveButton]}
                        disabled={!isFormValid}
                        onPress={handleRegister}
                    >
                        <Text style={styles.buttonText}>회원가입</Text>
                    </TouchableOpacity>
                </>
            )}
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    container: {
        padding: width * 0.05,
        backgroundColor: '#F6FAFE',
        flexGrow: 1,
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#000',
        marginBottom: 20,
        alignSelf: 'center',
    },
    inputWrapper: {
        position: 'relative',
        marginBottom: height * 0.02,
        justifyContent: 'center',
    },
    inputWithButton: {
        height: height * 0.065,
        backgroundColor: '#FFFFFF',
        borderRadius: 16,
        paddingLeft: 16,
        paddingRight: width * 0.24,
        fontSize: width * 0.038,
    },
    input: {
        height: height * 0.065,
        backgroundColor: '#FFFFFF',
        paddingHorizontal: width * 0.045,
        borderRadius: 16,
        fontSize: width * 0.038,
        marginBottom: height * 0.02,
    },
    inlineButton: {
        position: 'absolute',
        right: 8,
        top: 6,
        bottom: 6,
        width: width * 0.2,
        borderRadius: 12,
        backgroundColor: '#000',
        justifyContent: 'center',
        alignItems: 'center',
    },
    inlineButtonText: {
        color: '#fff',
        fontWeight: 'bold',
        fontSize: 13,
    },
    emailRow: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: height * 0.02,
        gap: 6,
    },
    emailInput: {
        flex: 1,
        height: height * 0.065,
        backgroundColor: '#FFFFFF',
        borderRadius: 16,
        paddingHorizontal: width * 0.035,
        fontSize: width * 0.038,
    },
    authButton: {
        width: '100%',
        height: height * 0.065,
        backgroundColor: '#000',
        borderRadius: 16,
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: height * 0.02,
    },
    nextButton: {
        backgroundColor: '#000',
        padding: 16,
        borderRadius: 16,
        marginTop: 16,
        alignItems: 'center',
        elevation: 3,
    },
    submitButton: {
        backgroundColor: '#000',
        padding: 16,
        borderRadius: 16,
        marginTop: 24,
        alignItems: 'center',
        elevation: 3,
    },
    inactiveButton: {
        backgroundColor: '#999',
    },
    buttonText: {
        fontSize: width * 0.042,
        fontWeight: 'bold',
        color: '#fff',
    },
    sectionTitle: {
        marginTop: 20,
        fontWeight: 'bold',
        fontSize: 16,
        marginBottom: 8,
        color: '#000',
    },
    profileImage: {
        width: 100,
        height: 100,
        borderRadius: 50,
        alignSelf: 'center',
        marginVertical: 16,
    },
    row: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        marginBottom: 10,
    },
    rowWrap: {
        flexDirection: 'row',
        flexWrap: 'wrap',
    },
    gradeButton: {
        paddingVertical: 10,
        paddingHorizontal: 16,
        borderRadius: 20,
        backgroundColor: '#FFFFFF',
        marginHorizontal: 5,
        borderWidth: 1,
        borderColor: '#DDD',
    },
    selected: {
        backgroundColor: '#000',
    },
    studyHabitPickerWrapper: {
        backgroundColor: '#fff',
        borderRadius: 16,
        overflow: 'hidden',
        height: height * 0.065,
        justifyContent: 'center',
        marginBottom: height * 0.02,
    },
    emailDomainPickerWrapper: {
        flex: 1,
        height: height * 0.065,
        backgroundColor: '#FFFFFF',
        borderRadius: 16,
        justifyContent: 'center',
        overflow: 'hidden',
    },
    picker: {
        fontSize: width * 0.038,
        height: '100%',
        width: '100%',
    },
    atSymbol: {
        fontSize: width * 0.045,
        fontWeight: 'bold',
        color: '#000',
        marginHorizontal: 6,
    },
    timerText: {
        position: 'absolute',
        right: width * 0.25,
        color: '#000',
        fontSize: width * 0.035,
    },
});

export default RegisterScreen;
