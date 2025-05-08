import React, { createContext, useContext, useState } from 'react';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState({
    name: '김종프님',
    greeting: '오늘도 화이팅하세요! 💪',
    profileImage: 'https://randomuser.me/api/portraits/men/1.jpg',
    email: 'jongproject@email.com',
  });

  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
};


export const useUser = () => useContext(UserContext);
