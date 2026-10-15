import React, { useState, useEffect } from 'react';
import './LandingPage.css';
import Navbar from '../components/Navbar';
import { Link } from 'react-router-dom';

const images = ['/assets/hero.jpg', '/assets/hero1.jpg', '/assets/hero2.jpg', '/assets/hero3.jpg'];

const LandingPage: React.FC = () => {
  const [quote, setQuote] = useState('');
  const [loading, setLoading] = useState(true);
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setQuote('Success is just failure that took a nap and woke up with a business plan.');
      setLoading(false);
    }, 1200);
    return () => clearTimeout(timeout);
  }, []);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % images.length);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const handlePrev = () => {
    setCurrentIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const handleNext = () => {
    setCurrentIndex((prev) => (prev + 1) % images.length);
  };

  return (
    <div className="landing-wrapper">
      <Navbar />

      <main className="main-content">
        <section className="hero-section">
          <div className="hero-text">
            <h1>
              We pretend to make money. <br></br>The investors are happy.
            </h1>
            <p className="subtitle">Helping you do things. Sometimes they're actually good.</p>
            <Link to="/login" className="cta-button">
              Explore the demo →
            </Link>
          </div>
          <div className="carousel">
            <button className="arrow left" aria-label="Previous image" onClick={handlePrev}>
              &#10094;
            </button>
            <img
              src={images[currentIndex]}
              alt={`Slide ${currentIndex + 1}`}
              className="carousel-image"
              key={currentIndex}
            />
            <button className="arrow right" aria-label="Next image" onClick={handleNext}>
              &#10095;
            </button>
          </div>
        </section>

        <section className="features-section">
          <h2>Why Choose Us?</h2>
          <ul>
            <li>✅ We use buzzwords like “synergy” and “blockchain”</li>
            <li>✅ A Spring Boot API you can explore locally</li>
            <li>✅ Our support team has doubled this year (hired one more person)</li>
            <li>✅ We have a roadmap, it’s just under construction</li>
          </ul>
        </section>

        <section className="quote-section">
          <h2>Daily Motivation</h2>
          {loading ? (
            <p>Loading wisdom from our pretend backend...</p>
          ) : (
            <blockquote>{quote}</blockquote>
          )}
        </section>
      </main>

      <footer className="footer">
        <p>&copy; {new Date().getFullYear()} PretendCorp™. All rights reserved-ish.</p>
      </footer>
    </div>
  );
};

export default LandingPage;
