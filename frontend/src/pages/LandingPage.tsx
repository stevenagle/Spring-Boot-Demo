import React from "react";
import styles from "./LandingPage.module.css";
import heroImage from "../assets/hero.jpg";

const LandingPage = () => {
  return (
    <div className={styles.container}>
      <nav className={styles.navbar}>
        <div className={styles.logo}>Fake Business</div>
        <ul className={styles.navLinks}>
          <li><a href="#">Home</a></li>
          <li><a href="#">About Me</a></li>
        </ul>
      </nav>

      <header className={styles.hero}>
        <div className={styles.heroText}>
          <h1>Welcome to my Spring Boot demo app.</h1>
          <p>This isn't a real company, but we can pretend it is.</p>
          <button className={styles.cta}>Get Started</button>
        </div>
        <img src={heroImage} alt="Team working together" className={styles.heroImage} />
      </header>
    </div>
  );
};

export default LandingPage;