import React from 'react';
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    Image,
    Dimensions,
} from 'react-native';

const { width, height } = Dimensions.get('window');

const IntroScreen = ({ navigation }) => {
    return (
        <View style={styles.background}>
            {/* 상단 콘텐츠 영역 */}
            <View style={styles.topContent}>
                <Image
                    source={require('../assets/intro.jpg')}
                    style={styles.logo}
                    resizeMode="contain"
                />

                <View style={styles.featureContainer}>
                    <View style={styles.featureCard}>
                        <Image source={require('../assets/personal.png')} style={styles.icon} />
                        <Text style={styles.iconLabel}>개인 학습</Text>
                    </View>
                    <View style={styles.featureCard}>
                        <Image source={require('../assets/group.png')} style={styles.icon} />
                        <Text style={styles.iconLabel}>팀 학습</Text>
                    </View>
                    <View style={styles.featureCard}>
                        <Image source={require('../assets/community.png')} style={styles.icon} />
                        <Text style={styles.iconLabel}>커뮤니티</Text>
                    </View>
                </View>
            </View>

            {/* 하단 버튼 영역 */}
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
        </View>
    );
};

const styles = StyleSheet.create({
    background: {
        flex: 1,
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: '#F6FAFE',
    },
    logo: {
        width: width * 0.5,
        height: height * 0.25,
        marginBottom: height * 0.05,
    },
    featureContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        width: width * 0.9,
        marginBottom: height * 0.06,
    },
    featureCard: {
        alignItems: 'center',
        backgroundColor: '#fff',
        borderRadius: 16,
        paddingVertical: height * 0.02,
        paddingHorizontal: width * 0.04,
        elevation: 4,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 3,
        width: width * 0.22,
    },
    icon: {
        width: width * 0.1,
        height: width * 0.1,
        marginBottom: 6,
    },
    topContent: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        paddingTop: height * 0.08,
    },
    iconLabel: {
        fontSize: 13,
        color: '#333',
        fontWeight: '500',
        textAlign: 'center',
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: width * 0.8,
        marginBottom: height * 0.05,
    },
    signUpButton: {
        flex: 1,
        borderWidth: 1,
        borderColor: '#000',
        backgroundColor: '#fff',
        borderRadius: 6,
        paddingVertical: height * 0.018,
        marginRight: 8,
        alignItems: 'center',
    },
    loginButton: {
        flex: 1,
        backgroundColor: '#000',
        borderRadius: 6,
        paddingVertical: height * 0.018,
        alignItems: 'center',
    },
    signUpText: {
        color: '#000',
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
