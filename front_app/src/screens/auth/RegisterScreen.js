import React, { useState, useEffect } from 'react';
import {
    View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView, Alert, Image,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { Picker } from '@react-native-picker/picker';
import { launchImageLibrary } from 'react-native-image-picker';

const RegisterScreen = () => {
    const [username, setUsername] = useState('');
    const [nickname, setNickname] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [emailId, setEmailId] = useState('');
    const [emailDomain, setEmailDomain] = useState('');
    const [isCustomDomain, setIsCustomDomain] = useState(false);
    const [authCode, setAuthCode] = useState('');
    const [timeLeft, setTimeLeft] = useState(300);
    const [grade, setGrade] = useState(null);
    const [subjects, setSubjects] = useState([]);
    const [studyHabit, setStudyHabit] = useState('');
    const [profileImage, setProfileImage] = useState(null);
    const [isFormValid, setIsFormValid] = useState(false);

    const allSubjects = ['국어', '수학', '영어', '과학', '사회'];
    const habits = ['새벽형', '야행성', '주말 집중형', '루틴형'];

    useEffect(() => {
        setIsFormValid(
            username && nickname && password && passwordConfirm &&
            password === passwordConfirm &&
            emailId && emailDomain && authCode &&
            grade && subjects.length > 0 && studyHabit
        );
    }, [username, nickname, password, passwordConfirm, emailId, emailDomain, authCode, grade, subjects, studyHabit]);

    useEffect(() => {
        if (timeLeft > 0) {
            const timer = setTimeout(() => setTimeLeft(prev => prev - 1), 1000);
            return () => clearTimeout(timer);
        }
    }, [timeLeft]);

    const pickImage = () => {
        launchImageLibrary({ mediaType: 'photo' }, (res) => {
            if (!res.didCancel && res.assets?.length > 0) {
                setProfileImage(res.assets[0].uri);
            }
        });
    };

    const toggleSubject = (subj) => {
        setSubjects(prev =>
            prev.includes(subj) ? prev.filter(s => s !== subj) : [...prev, subj]
        );
    };

    return (
        <LinearGradient colors={['#E3F2FD', '#FFFFFF']} style={styles.container}>
            <ScrollView contentContainerStyle={styles.scroll}>
                <Text style={styles.title}>회원가입</Text>

                {/* 아이디 */}
                <View style={styles.inputWrapper}>
                    <Image source={require('../../assets/icon-id.png')} style={styles.inputIcon} />
                    <TextInput style={styles.inputField} placeholder="아이디" value={username} onChangeText={setUsername} />
                </View>

                {/* 비밀번호 */}
                <View style={styles.inputWrapper}>
                    <Image source={require('../../assets/icon-lock.png')} style={styles.inputIcon} />
                    <TextInput style={styles.inputField} placeholder="비밀번호" secureTextEntry value={password} onChangeText={setPassword} />
                </View>
                <View style={styles.inputWrapper}>
                    <Image source={require('../../assets/icon-lock.png')} style={styles.inputIcon} />
                    <TextInput style={styles.inputField} placeholder="비밀번호 확인" secureTextEntry value={passwordConfirm} onChangeText={setPasswordConfirm} />
                </View>
                {passwordConfirm.length > 0 && (
                    <Text style={styles.helper}>
                        {password === passwordConfirm ? '✔ 비밀번호 일치' : '✘ 비밀번호 불일치'}
                    </Text>
                )}

                {/* 닉네임 */}
                <View style={styles.inputWrapper}>
                    <Image source={require('../../assets/icon-user.png')} style={styles.inputIcon} />
                    <TextInput style={styles.inputField} placeholder="닉네임" value={nickname} onChangeText={setNickname} />
                </View>

                {/* 이메일 */}
                <View style={styles.row}>
                    <TextInput style={[styles.input, { flex: 1.3 }]} placeholder="이메일 아이디" value={emailId} onChangeText={setEmailId} />
                    <Text style={styles.at}>@</Text>
                    {isCustomDomain ? (
                        <TextInput style={[styles.input, { flex: 1.7 }]} placeholder="도메인 입력" value={emailDomain} onChangeText={setEmailDomain} />
                    ) : (
                        <View style={{ flex: 1.7, backgroundColor: '#fff', borderRadius: 10 }}>
                            <Picker selectedValue={emailDomain} onValueChange={(val) => {
                                if (val === 'custom') {
                                    setIsCustomDomain(true);
                                    setEmailDomain('');
                                } else {
                                    setEmailDomain(val);
                                    setIsCustomDomain(false);
                                }
                            }}>
                                <Picker.Item label="도메인 선택" value="" />
                                <Picker.Item label="gmail.com" value="gmail.com" />
                                <Picker.Item label="naver.com" value="naver.com" />
                                <Picker.Item label="직접 입력" value="custom" />
                            </Picker>
                        </View>
                    )}
                </View>

                {/* 인증 */}
                <TouchableOpacity style={styles.button} onPress={() => setTimeLeft(300)}>
                    <Text style={styles.buttonText}>인증번호 발송</Text>
                </TouchableOpacity>
                <View style={styles.row}>
                    <TextInput style={[styles.input, { flex: 2 }]} placeholder="인증번호" value={authCode} onChangeText={setAuthCode} />
                    <Text style={styles.timer}>{Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}</Text>
                    <TouchableOpacity style={styles.buttonMini}><Text style={styles.buttonText}>확인</Text></TouchableOpacity>
                </View>

                {/* 학년 */}
                <Text style={styles.sectionTitle}>학년</Text>
                <View style={styles.row}>
                    {[1, 2, 3].map((g) => (
                        <TouchableOpacity key={g} style={[styles.radio, grade === g && styles.radioSelected]} onPress={() => setGrade(g)}>
                            <Text style={{ color: grade === g ? '#fff' : '#007BFF' }}>{g}학년</Text>
                        </TouchableOpacity>
                    ))}
                </View>

                {/* 과목 */}
                <Text style={styles.sectionTitle}>과목 선택</Text>
                <View style={styles.subjects}>
                    {allSubjects.map((s) => (
                        <TouchableOpacity key={s} style={[styles.tag, subjects.includes(s) && styles.tagSelected]} onPress={() => toggleSubject(s)}>
                            <Text style={[styles.tagText, subjects.includes(s) && styles.tagTextSelected]}>{s}</Text>
                        </TouchableOpacity>
                    ))}
                </View>

                {/* 습관 */}
                <Text style={styles.sectionTitle}>공부 습관</Text>
                <View style={styles.pickerWrapper}>
                    <Picker selectedValue={studyHabit} onValueChange={setStudyHabit} style={styles.picker}>
                        <Picker.Item label="선택" value="" />
                        {habits.map(h => <Picker.Item key={h} label={h} value={h} />)}
                    </Picker>
                </View>

                {/* 프로필 이미지 */}
                <Text style={styles.sectionTitle}>프로필 이미지</Text>
                <View style={styles.imageBox}>
                    <Image source={profileImage ? { uri: profileImage } : require('../../assets/profile.jpg')} style={styles.profileImage} />
                    <TouchableOpacity style={styles.imageOverlay} onPress={pickImage}>
                        <Text style={styles.overlayText}>+</Text>
                    </TouchableOpacity>
                </View>

                {/* 회원가입 */}
                <TouchableOpacity style={[styles.button, !isFormValid && styles.buttonDisabled]} disabled={!isFormValid}>
                    <Text style={styles.buttonText}>회원가입</Text>
                </TouchableOpacity>
            </ScrollView>
        </LinearGradient>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1 },
    scroll: { padding: 24, alignItems: 'center' },
    title: { fontSize: 28, fontWeight: 'bold', color: '#007BFF', marginBottom: 20 },
    inputWrapper: {
        flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 12,
        paddingHorizontal: 14, marginBottom: 16, elevation: 3,
    },
    inputIcon: { width: 20, height: 20, marginRight: 10 },
    inputField: { flex: 1, height: 48 },
    input: {
        backgroundColor: '#fff', borderRadius: 12, paddingHorizontal: 14,
        height: 48, marginBottom: 12, elevation: 2,
    },
    row: { flexDirection: 'row', alignItems: 'center', width: '100%', gap: 8, marginBottom: 12 },
    at: { fontSize: 16, fontWeight: 'bold', color: '#555' },
    button: {
        width: '100%', height: 48, backgroundColor: '#007BFF', borderRadius: 25,
        justifyContent: 'center', alignItems: 'center', marginVertical: 10,
        elevation: 4,
    },
    buttonMini: {
        height: 48, paddingHorizontal: 16, backgroundColor: '#007BFF',
        borderRadius: 12, justifyContent: 'center', alignItems: 'center',
    },
    buttonText: { color: '#FFF', fontWeight: 'bold', fontSize: 16 },
    buttonDisabled: { backgroundColor: '#B0C4DE' },
    timer: { color: '#FF3333', fontSize: 14 },
    sectionTitle: {
        alignSelf: 'flex-start', fontSize: 16, fontWeight: 'bold',
        marginTop: 20, marginBottom: 8, color: '#007BFF'
    },
    radio: {
        flex: 1, paddingVertical: 10, borderRadius: 12, borderWidth: 1,
        borderColor: '#007BFF', justifyContent: 'center', alignItems: 'center',
    },
    radioSelected: { backgroundColor: '#007BFF' },
    subjects: { flexDirection: 'row', flexWrap: 'wrap' },
    tag: {
        paddingHorizontal: 14, paddingVertical: 8, borderRadius: 20,
        backgroundColor: '#F1F5FB', marginRight: 10, marginBottom: 10,
    },
    tagSelected: { backgroundColor: '#007BFF' },
    tagText: { fontSize: 14, color: '#444' },
    tagTextSelected: { color: '#fff', fontWeight: 'bold' },
    pickerWrapper: { width: '100%', backgroundColor: '#fff', borderRadius: 10, elevation: 2 },
    picker: { height: 48, width: '100%' },
    imageBox: { position: 'relative', marginBottom: 24 },
    profileImage: { width: 120, height: 120, borderRadius: 60, borderWidth: 2, borderColor: '#007BFF', backgroundColor: '#eee' },
    imageOverlay: {
        position: 'absolute', bottom: 0, right: 0, backgroundColor: '#007BFF',
        width: 36, height: 36, borderRadius: 18, justifyContent: 'center', alignItems: 'center',
    },
    overlayText: { color: '#fff', fontSize: 24, fontWeight: 'bold' },
});

export default RegisterScreen;
