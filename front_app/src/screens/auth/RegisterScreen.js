import React, { useState, useEffect } from 'react';
import {
    View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView, Alert, Image, Dimensions
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { launchImageLibrary } from 'react-native-image-picker';
import api from '../../api/api';

const { width, height } = Dimensions.get('window');

const habits = ['새벽형', '야행성', '주말 집중형', '루틴형'];
const allSubjects = ['국어', '수학', '영어', '과학', '사회', '기타'];

const RegisterScreen = ({ navigation }) => {
    const [step, setStep] = useState(1);
    const [username, setUsername] = useState('');
    const [isUsernameValid, setIsUsernameValid] = useState(null);
    const [nickname, setNickname] = useState('');
    const [isNicknameValid, setIsNicknameValid] = useState(null);
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [isPasswordMatch, setIsPasswordMatch] = useState(null);
    const [email, setEmail] = useState('');
    const [emailDomain, setEmailDomain] = useState('');
    const [isCustomDomain, setIsCustomDomain] = useState(false);
    const [authCode, setAuthCode] = useState('');
    const [isAuthSent, setIsAuthSent] = useState(false);
    const [authVerified, setAuthVerified] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0);

    const [grade, setGrade] = useState(null);
    const [subjects, setSubjects] = useState([]);
    const [newSubject, setNewSubject] = useState('');
    const [studyHabit, setStudyHabit] = useState('');
    const [profileImage, setProfileImage] = useState(null);
    const [isFormValid, setIsFormValid] = useState(false);

    useEffect(() => {
        setIsFormValid(
            username && isUsernameValid && nickname && isNicknameValid &&
            password && passwordConfirm && isPasswordMatch &&
            email && emailDomain && authCode && authVerified &&
            grade && subjects.length > 0 && studyHabit
        );
    }, [username, isUsernameValid, nickname, isNicknameValid, password, passwordConfirm, isPasswordMatch, email, emailDomain, authCode, authVerified, grade, subjects, studyHabit]);

    useEffect(() => {
        if (timeLeft > 0) {
            const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
            return () => clearTimeout(timer);
        }
    }, [timeLeft]);

    const checkUsername = async () => {
        try {
            const res = await api.post('/auth/check-username', { username });
            setIsUsernameValid(res.data.data);
            Alert.alert('결과', res.data.data ? '사용 가능한 아이디입니다.' : '이미 사용 중입니다.');
        } catch {
            Alert.alert('오류', '아이디 확인 중 오류 발생');
        }
    };

    const checkNickname = async () => {
        try {
            const res = await api.post('/auth/check-nickname', { nickname });
            setIsNicknameValid(res.data.data);
            Alert.alert('결과', res.data.data ? '사용 가능한 닉네임입니다.' : '이미 사용 중입니다.');
        } catch {
            Alert.alert('오류', '닉네임 확인 중 오류 발생');
        }
    };

    const sendVerificationCode = async () => {
        try {
            const res = await api.post('/auth/send-code', { email: `${email}@${emailDomain}` });
            setIsAuthSent(true);
            setTimeLeft(300);
            Alert.alert('성공', '인증번호가 발송되었습니다.');
        } catch {
            Alert.alert('오류', '이메일 인증 중 오류 발생');
        }
    };

    const verifyCode = async () => {
        try {
            const res = await api.post('/auth/verify-code', { email: `${email}@${emailDomain}`, code: authCode });
            setAuthVerified(res.data.data);
            Alert.alert('결과', res.data.data ? '인증 성공' : '인증 실패');
        } catch {
            Alert.alert('오류', '코드 확인 중 오류 발생');
        }
    };

    const toggleSubject = (subj) => {
        setSubjects(prev => prev.includes(subj) ? prev.filter(s => s !== subj) : [...prev, subj]);
    };

    const pickImage = () => {
        launchImageLibrary({ mediaType: 'photo' }, (res) => {
            if (!res.didCancel && res.assets?.length > 0) {
                setProfileImage(res.assets[0].uri);
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
        subjects.forEach(s => formData.append('subjects', s));
        formData.append('studyHabit', studyHabit);
        if (profileImage) {
            const name = profileImage.split('/').pop();
            formData.append('profileImage', { uri: profileImage, name, type: 'image/jpeg' });
        }

        try {
            await api.post('/auth/register', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            Alert.alert('회원가입 완료', '이제 로그인하세요.');
            navigation.navigate('Login');
        } catch {
            Alert.alert('오류', '회원가입 중 문제가 발생했습니다.');
        }
    };

    return (
        <ScrollView contentContainerStyle={styles.container}>
            <Text style={styles.title}>회원가입</Text>

            {step === 1 ? (
                <>
                    {/* 아이디, 닉네임, 비밀번호, 이메일 인증 */}
                    <View style={styles.inputRow}>
                        <TextInput style={styles.input} placeholder="아이디" value={username} onChangeText={setUsername} />
                        <TouchableOpacity style={styles.checkButton} onPress={checkUsername}><Text>중복확인</Text></TouchableOpacity>
                    </View>

                    <View style={styles.inputRow}>
                        <TextInput style={styles.input} placeholder="닉네임" value={nickname} onChangeText={setNickname} />
                        <TouchableOpacity style={styles.checkButton} onPress={checkNickname}><Text>중복확인</Text></TouchableOpacity>
                    </View>

                    <TextInput style={styles.input} placeholder="비밀번호" secureTextEntry value={password} onChangeText={setPassword} />
                    <TextInput style={styles.input} placeholder="비밀번호 확인" secureTextEntry value={passwordConfirm} onChangeText={text => { setPasswordConfirm(text); setIsPasswordMatch(text === password); }} />

                    <View style={styles.inputRow}>
                        <TextInput style={styles.input} placeholder="이메일 아이디" value={email} onChangeText={setEmail} />
                        <Text>@</Text>
                        {isCustomDomain ? (
                            <TextInput style={styles.input} value={emailDomain} onChangeText={setEmailDomain} />
                        ) : (
                            <Picker selectedValue={emailDomain} onValueChange={(val) => {
                                if (val === 'custom') { setIsCustomDomain(true); setEmailDomain(''); }
                                else { setIsCustomDomain(false); setEmailDomain(val); }
                            }} style={{ flex: 1 }}>
                                <Picker.Item label="도메인 선택" value="" />
                                <Picker.Item label="gmail.com" value="gmail.com" />
                                <Picker.Item label="naver.com" value="naver.com" />
                                <Picker.Item label="직접입력" value="custom" />
                            </Picker>
                        )}
                    </View>

                    <TouchableOpacity style={styles.checkButton} onPress={sendVerificationCode}><Text>인증번호 발송</Text></TouchableOpacity>
                    {isAuthSent && (
                        <View style={styles.inputRow}>
                            <TextInput style={styles.input} placeholder="인증번호" value={authCode} onChangeText={setAuthCode} />
                            <TouchableOpacity style={styles.checkButton} onPress={verifyCode}><Text>확인</Text></TouchableOpacity>
                            <Text style={{ color: 'red' }}>{Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}</Text>
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
                        {[1, 2, 3].map((g) => (
                            <TouchableOpacity key={g} style={[styles.gradeButton, grade === g && styles.selected]} onPress={() => setGrade(g)}>
                                <Text>{g}학년</Text>
                            </TouchableOpacity>
                        ))}
                    </View>

                    <Text style={styles.sectionTitle}>과목 선택</Text>
                    <View style={styles.rowWrap}>
                        {allSubjects.map(s => (
                            <TouchableOpacity key={s} style={[styles.tag, subjects.includes(s) && styles.tagSelected]} onPress={() => toggleSubject(s)}>
                                <Text>{s}</Text>
                            </TouchableOpacity>
                        ))}
                    </View>

                    <Text style={styles.sectionTitle}>공부 습관</Text>
                    <Picker selectedValue={studyHabit} onValueChange={setStudyHabit}>
                        <Picker.Item label="선택" value="" />
                        {habits.map(h => <Picker.Item key={h} label={h} value={h} />)}
                    </Picker>

                    <Text style={styles.sectionTitle}>프로필 이미지</Text>
                    <TouchableOpacity onPress={pickImage}>
                        <Image source={profileImage ? { uri: profileImage } : require('../../assets/profile.jpg')} style={styles.profileImage} />
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
    container: { padding: 20, backgroundColor: '#E3F2FD', flexGrow: 1 },
    title: { fontSize: 24, fontWeight: 'bold', color: '#007BFF', marginBottom: 20 },
    input: { flex: 1, backgroundColor: '#fff', padding: 10, marginVertical: 8, borderRadius: 8 },
    inputRow: { flexDirection: 'row', alignItems: 'center', gap: 10 },
    checkButton: { backgroundColor: '#007BFF', padding: 10, borderRadius: 8 },
    nextButton: { backgroundColor: '#007BFF', padding: 15, borderRadius: 8, marginTop: 20 },
    submitButton: { backgroundColor: '#007BFF', padding: 15, borderRadius: 8, marginTop: 20 },
    inactiveButton: { backgroundColor: '#999' },
    buttonText: { color: '#fff', fontWeight: 'bold', textAlign: 'center' },
    sectionTitle: { marginTop: 20, fontWeight: 'bold' },
    row: { flexDirection: 'row', justifyContent: 'space-around', marginVertical: 10 },
    rowWrap: { flexDirection: 'row', flexWrap: 'wrap' },
    gradeButton: { padding: 10, borderWidth: 1, borderRadius: 8 },
    selected: { backgroundColor: '#007BFF', color: '#fff' },
    tag: { padding: 8, margin: 5, backgroundColor: '#eee', borderRadius: 16 },
    tagSelected: { backgroundColor: '#007BFF' },
    profileImage: { width: 100, height: 100, borderRadius: 50, alignSelf: 'center', marginVertical: 10 },
});

export default RegisterScreen;
