import React from 'react';
import './AboutPage.css';
import Navbar from '../components/Navbar';

const AboutPage: React.FC = () => {
  return (
    <>
      <Navbar />
      <div className="about-container">
        <h1>About This Project</h1>
        <p>
          This is a mock front-end application built for local development and testing of a Spring Boot backend API.
          It’s designed to simulate a user experience without the pressure of production-grade polish.
        </p>

        <h2>Architecture Overview</h2>
        <ul>
          <li>🧠 Frontend: React + TypeScript, styled with CSS modules</li>
          <li>🚀 Backend: Spring Boot REST API</li>
          <li>🔐 Login: Fake authentication using test email addresses</li>
          <li>🏠 User Portal: Displays mock profile data after login</li>
        </ul>

        <h2>How It Works</h2>
        <p>
          Logging in to the user portal with a valid test email address will take you to that user's <strong>User Home</strong> page.
          There, you’ll see profile information pulled from the backend.
        </p>

        <p className="disclaimer">
          Don’t judge the design skills here. This was never intended to be a production-grade website!
        </p>
      </div>
    </>
  );
};

export default AboutPage;
