import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Image } from 'react-native';

import HomeScreen from './src/screens/home/HomeScreen';
import CommunityScreen from './src/screens/community/CommunityMainScreen';
import PersonalStudyMainScreen from './src/screens/study/PersonalStudyMainScreen';
import PersonalStudyScreen from './src/screens/study/PersonalStudyScreen';  // 추가
import TeamStudyScreen from './src/screens/study/TeamStudyScreen';  // 추가
import UnitTestScreen from './src/screens/unitTest/UnitTestScreen';
import MyPageMainScreen from './src/screens/mypage/MyPageMainScreen';
import IntroScreen from './src/screens/IntroScreen';
import LoginScreen from './src/screens/auth/LoginScreen';
import RegisterScreen from './src/screens/auth/RegisterScreen';
import FindAccountScreen from "./src/screens/auth/FindAccountScreen";
import ResetPasswordScreen from "./src/screens/auth/ResetPasswordScreen";

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const screenOptions = ({ route }) => ({
    tabBarIcon: ({ focused, size }) => {
        let iconPath;

        switch (route.name) {
            case '공부시작':
                iconPath = require('./src/assets/personal.png');
                break;
            case '커뮤니티':
                iconPath = require('./src/assets/community.png');
                break;
            case '홈':
                iconPath = require('./src/assets/home.png');
                break;
            case '단원평가':
                iconPath = require('./src/assets/unitTest.png');
                break;
            case '마이페이지':
                iconPath = require('./src/assets/mypage.png');
                break;
        }

        return (
            <Image
                source={iconPath}
                style={{
                    width: size,
                    height: size,
                    tintColor: focused ? '#007AFF' : '#8E8E93'
                }}
            />
        );
    },
    tabBarShowLabel: true,
    tabBarActiveTintColor: '#007AFF',
    tabBarInactiveTintColor: '#8E8E93',
});

// 탭 네비게이터 설정
const MainTabNavigator = () => (
    <Tab.Navigator initialRouteName="홈" screenOptions={screenOptions}>
        <Tab.Screen name="공부시작" component={PersonalStudyMainScreen} />
        <Tab.Screen name="커뮤니티" component={CommunityScreen} />
        <Tab.Screen name="홈" component={HomeScreen} />
        <Tab.Screen name="단원평가" component={UnitTestScreen} />
        <Tab.Screen name="마이페이지" component={MyPageMainScreen} />
    </Tab.Navigator>
);

const App = () => (
    <NavigationContainer>
        <Stack.Navigator initialRouteName="Intro" screenOptions={{ headerShown: false }}>
            <Stack.Screen name="Intro" component={IntroScreen} />
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen name="Register" component={RegisterScreen} />
            <Stack.Screen name="FindAccount" component={FindAccountScreen} />
            <Stack.Screen name="ResetPassword" component={ResetPasswordScreen} />
            <Stack.Screen name="Main" component={MainTabNavigator} />
            {/* 추가된 개인 공부, 팀 공부 화면 */}
            <Stack.Screen name="PersonalStudyScreen" component={PersonalStudyScreen} />
            <Stack.Screen name="TeamStudyScreen" component={TeamStudyScreen} />
        </Stack.Navigator>
    </NavigationContainer>
);

export default App;