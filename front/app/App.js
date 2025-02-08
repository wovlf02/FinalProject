import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

// 스크린 컴포넌트 가져오기
import IntroScreen from './src/screens/IntroScreen';
import LoginScreen from './src/screens/auth/LoginScreen';
import RegisterScreen from './src/screens/auth/RegisterScreen';
import FindAccountScreen from "./src/screens/auth/FindAccountScreen";
import ResetPasswordScreen from "./src/screens/auth/ResetPasswordScreen";
import HomeScreen from "./src/screens/home/HomeScreen";

const Stack = createStackNavigator();

export default function App() {
    return (
        <NavigationContainer>
            <Stack.Navigator initialRouteName="Intro" screenOptions={{ headerShown: false }}>
                {/* 최초 인트로 화면 */}
                <Stack.Screen name="Intro" component={IntroScreen} />

                {/* 로그인 화면 */}
                <Stack.Screen name="Login" component={LoginScreen} />

                {/* 회원가입 화면 */}
                <Stack.Screen name="Register" component={RegisterScreen} />

                {/* 계정 찾기 화면 */}
                <Stack.Screen name="FindAccount" component={FindAccountScreen} />

                {/* 비밀번호 재설정 화면 */}
                <Stack.Screen name="ResetPassword" component={ResetPasswordScreen} />

                {/* 홈 화면 */}
                <Stack.Screen name="Home" component={HomeScreen} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}
