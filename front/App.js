import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Image } from 'react-native';
import HomeScreen from './src/screens/home/HomeScreen';
import CommunityScreen from './src/screens/community/CommunityMainScreen';
import PersonalStudyMainScreen from './src/screens/personal/PersonalStudyMainScreen';
import GroupStudyMainScreen from './src/screens/group/GroupStudyMainScreen';
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
            case 'Home':
                iconPath = require('./src/assets/home.png');
                break;
            case 'Community':
                iconPath = require('./src/assets/community.png');
                break;
            case 'Personal Learning':
                iconPath = require('./src/assets/personal.png');
                break;
            case 'Group Learning':
                iconPath = require('./src/assets/group.png');
                break;
            case 'My Page':
                iconPath = require('./src/assets/mypage.png');
                break;
        }
        return <Image source={iconPath} style={{ width: size, height: size, tintColor: focused ? '#007AFF' : '#8E8E93' }} />;
    },
    tabBarShowLabel: true,
    tabBarActiveTintColor: '#007AFF',
    tabBarInactiveTintColor: '#8E8E93',
});

const MainTabNavigator = ({ route }) => (
    <Tab.Navigator screenOptions={screenOptions}>
        <Tab.Screen
            name="Home"
            component={HomeScreen}
            initialParams={route.params} // 🔹 로그인 후 전달받은 데이터를 HomeScreen으로 전달
        />
        <Tab.Screen name="Community" component={CommunityScreen} />
        <Tab.Screen name="Personal Learning" component={PersonalStudyMainScreen} />
        <Tab.Screen name="Group Learning" component={GroupStudyMainScreen} />
        <Tab.Screen name="My Page" component={MyPageMainScreen} />
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
            <Stack.Screen
                name="Main"  // 🔹 기존의 "Home"을 "MainTabs"로 변경하여 네비게이션 충돌 방지
                component={MainTabNavigator}
                options={{ headerShown: false }}
            />
        </Stack.Navigator>
    </NavigationContainer>
);

export default App;
