import React from 'react';
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    Image,
    Dimensions,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

const { width } = Dimensions.get('window');

const IntroScreen = ({ navigation }) => {
    return (
        <LinearGradient
            colors={['#E3F2FD', '#FFFFFF']}
            style={styles.background}
        >
            {/* 로고 및 앱명 */}
            <View style={styles.logoContainer}>
                <Image
                    source={require('../assets/intro.png')}
                    style={styles.logo}
                    resizeMode="contain"
                />
                <Text style={styles.appName}>함캠</Text>
                <Text style={styles.slogan}>함께해요, 캠스터디</Text>
            </View>

            {/* 주요 기능 카드 */}
            <View style={styles.featureContainer}>
                <View style={styles.featureCard}>
                    <Image source={require('../assets/community.png')} style={styles.icon} />
                    <Text style={styles.iconLabel}>커뮤니티</Text>
                </View>
                <View style={styles.featureCard}>
                    <Image source={require('../assets/personal.png')} style={styles.icon} />
                    <Text style={styles.iconLabel}>개인 학습</Text>
                </View>
                <View style={styles.featureCard}>
                    <Image source={require('../assets/group.png')} style={styles.icon} />
                    <Text style={styles.iconLabel}>그룹 학습</Text>
                </View>
            </View>

            {/* 버튼 */}
            <View style={styles.buttonContainer}>
                <TouchableOpacity
                    style={styles.signUpButton}
                    onPress={() => navigation.navigate('Register')}
                >
                    <Text style={styles.signUpText}>회원가입</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={styles.loginButton}
                    onPress={() => navigation.navigate('Login')}
                >
                    <Text style={styles.loginText}>로그인</Text>
                </TouchableOpacity>
            </View>
        </LinearGradient>
    );
};

const styles = StyleSheet.create({
    background: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    logoContainer: {
        alignItems: 'center',
        marginBottom: 40,
    },
    logo: {
        width: 100,
        height: 100,
    },
    appName: {
        fontSize: 36,
        fontWeight: 'bold',
        color: '#007BFF',
        marginTop: 10,
    },
    slogan: {
        fontSize: 16,
        color: '#666',
        marginTop: 4,
    },
    featureContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        width: width * 0.85,
        marginVertical: 30,
    },
    featureCard: {
        alignItems: 'center',
        backgroundColor: '#FFF',
        borderRadius: 16,
        padding: 12,
        elevation: 4,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 3,
        width: 90,
    },
    icon: {
        width: 50,
        height: 50,
        marginBottom: 6,
    },
    iconLabel: {
        fontSize: 14,
        color: '#333',
        fontWeight: '500',
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '80%',
        marginTop: 20,
    },
    signUpButton: {
        flex: 1,
        borderWidth: 2,
        borderColor: '#007BFF',
        backgroundColor: '#FFF',
        borderRadius: 30,
        paddingVertical: 14,
        marginRight: 10,
        alignItems: 'center',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 3,
        elevation: 4,
    },
    loginButton: {
        flex: 1,
        backgroundColor: '#007BFF',
        borderRadius: 30,
        paddingVertical: 14,
        alignItems: 'center',
        elevation: 4,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 3,
    },
    signUpText: {
        color: '#007BFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
    loginText: {
        color: '#FFF',
        fontSize: 16,
        fontWeight: 'bold',
    },
});

export default IntroScreen;
