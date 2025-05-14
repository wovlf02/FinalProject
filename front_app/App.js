import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Image, View, Text, TouchableOpacity, StyleSheet } from 'react-native';

import HomeScreen from './src/screens/home/HomeScreen';
import PersonalStudyMainScreen from './src/screens/study/PersonalStudyMainScreen';
import PersonalStudyScreen from './src/screens/study/PersonalStudyScreen';
import TeamStudyScreen from './src/screens/study/TeamStudyScreen';
import UnitTestScreen from './src/screens/unitTest/UnitTestScreen';
import MyPageMainScreen from './src/screens/mypage/MyPageMainScreen';
import IntroScreen from './src/screens/IntroScreen';
import LoginScreen from './src/screens/auth/LoginScreen';
import RegisterScreen from './src/screens/auth/RegisterScreen';
import FindAccountScreen from './src/screens/auth/FindAccountScreen';
import ResetPasswordScreen from './src/screens/auth/ResetPasswordScreen';

import { UserProvider } from './src/contexts/UserContext';

import PostListScreen from './src/screens/community/PostListScreen';
import CreatePostScreen from './src/screens/community/CreatePostScreen';
import PostDetailScreen from './src/screens/community/PostDetailScreen';
import PostEditScreen from './src/screens/community/PostEditScreen';
import ChatRoomListScreen from './src/screens/community/ChatRoomListScreen';
import ChatRoomScreen from './src/screens/community/ChatRoomScreen';
import FriendListScreen from './src/screens/community/FriendListScreen';
import FriendSearchScreen from './src/screens/community/FriendSearchScreen';
import FriendRequestScreen from './src/screens/community/FriendRequestScreen';
import FriendBlockListScreen from './src/screens/community/FriendBlockListScreen';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();
const CommunityStack = createStackNavigator();

const CommunityNavigator = () => (
    <CommunityStack.Navigator screenOptions={{ headerShown: false }}>
      <CommunityStack.Screen name="PostList" component={PostListScreen} />
      <CommunityStack.Screen name="CreatePost" component={CreatePostScreen} />
      <CommunityStack.Screen name="PostDetail" component={PostDetailScreen} />
      <CommunityStack.Screen name="PostEdit" component={PostEditScreen} />
      <CommunityStack.Screen name="ChatRoomList" component={ChatRoomListScreen} />
      <CommunityStack.Screen name="ChatRoom" component={ChatRoomScreen} />
      <CommunityStack.Screen name="FriendList" component={FriendListScreen} />
      <CommunityStack.Screen name="FriendSearch" component={FriendSearchScreen} />
      <CommunityStack.Screen name="FriendRequest" component={FriendRequestScreen} />
      <CommunityStack.Screen name="FriendBlockList" component={FriendBlockListScreen} />
    </CommunityStack.Navigator>
);

const screenOptions = ({ route }) => ({
  tabBarIcon: ({ focused }) => {
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
              width: 24,
              height: 24,
              resizeMode: 'contain',
              tintColor: focused ? '#007AFF' : undefined, // 선택 시만 컬러 적용
            }}
        />
    );
  },
  headerShown: false, // 상단 타이틀 숨기기
  tabBarShowLabel: true,
  tabBarActiveTintColor: '#007AFF',
  tabBarInactiveTintColor: undefined, // 아이콘 원본 색 유지
});

const MainTabNavigator = () => (
    <Tab.Navigator
        initialRouteName="홈"
        screenOptions={{ headerShown: false }}
        tabBar={({ state, descriptors, navigation }) => (
            <View style={styles.tabContainer}>
              {state.routes.map((route, index) => {
                const isFocused = state.index === index;
                const { options } = descriptors[route.key];
                const iconMap = {
                  '공부시작': require('./src/assets/personal.png'),
                  '커뮤니티': require('./src/assets/community.png'),
                  '홈': require('./src/assets/home.png'),
                  '단원평가': require('./src/assets/unitTest.png'),
                  '마이페이지': require('./src/assets/mypage.png'),
                };

                return (
                    <TouchableOpacity
                        key={route.name}
                        accessibilityRole="button"
                        onPress={() => navigation.navigate(route.name)}
                        style={[
                          styles.tabItem,
                          isFocused && styles.focusedTabItem,
                        ]}
                    >
                      <Image source={iconMap[route.name]} style={styles.icon} resizeMode="contain" />
                      <Text style={styles.label}>{route.name}</Text>
                    </TouchableOpacity>
                );
              })}
            </View>
        )}
    >
      <Tab.Screen name="공부시작" component={PersonalStudyMainScreen} />
      <Tab.Screen name="커뮤니티" component={CommunityNavigator} />
      <Tab.Screen name="홈" component={HomeScreen} />
      <Tab.Screen name="단원평가" component={UnitTestScreen} />
      <Tab.Screen name="마이페이지" component={MyPageMainScreen} />
    </Tab.Navigator>
);

const App = () => (
    <UserProvider>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Intro" screenOptions={{ headerShown: false }}>
          <Stack.Screen name="Intro" component={IntroScreen} />
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
          <Stack.Screen name="FindAccount" component={FindAccountScreen} />
          <Stack.Screen name="ResetPassword" component={ResetPasswordScreen} />
          <Stack.Screen name="Main" component={MainTabNavigator} />
          <Stack.Screen name="PersonalStudyScreen" component={PersonalStudyScreen} />
          <Stack.Screen name="TeamStudyScreen" component={TeamStudyScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </UserProvider>
);

const styles = StyleSheet.create({
  tabContainer: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderTopWidth: 1,
    borderTopColor: '#ddd',
    height: 64,
  },
  tabItem: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  focusedTabItem: {
    backgroundColor: '#E6F0FF', // 선택된 탭 배경색
  },
  icon: {
    width: 26,
    height: 26,
    marginBottom: 2,
  },
  label: {
    fontSize: 12,
    color: '#222', // 텍스트 색상 고정
  },
});


export default App;
