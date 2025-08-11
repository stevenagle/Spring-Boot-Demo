import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import AboutPage from './pages/AboutPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';

import { getCookie } from './utils/cookies';
import { loadCurrentUser, saveCurrentUser } from './utils/session';
import { getUser } from './api/UserApi';

const App: React.FC = () => {
  useEffect(() => {
    const username = getCookie('demoUser');
    if (username && !loadCurrentUser()) {
      // Optionally, fetch fresh profile from API to repopulate session storage
      getUser(username).then(saveCurrentUser);
    }
  }, []);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/about" element={<AboutPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/profile" element={<ProfilePage />} />
      </Routes>
    </Router>
  );
};

export default App;
