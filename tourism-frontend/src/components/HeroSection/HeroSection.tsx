import React from 'react';
import './HeroSection.scss';
import mandelaVideo from "../../assets/videos/mandela-rugby.mp4";

/**
 * HeroSection Component
 *
 * Displays a fullscreen hero section with a video background, headline, subtitle, and buttons.
 *
 * @returns {JSX.Element} The hero section component.
 */
const HeroSection: React.FC = () => {
  return (
    <section className="hero-section horizontal-scroll-item">
      {/* Fullscreen Video Background */}
      <video className="hero-video" autoPlay muted loop>
        <source src={mandelaVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>

      {/* Overlay Content */}
      <div className="hero-overlay">
        <h1 className="hero-title">Rugby: Uniting South Africa, One Match at a Time</h1>
        <p className="hero-subtitle">
          Explore the stories, players, and milestones that shaped a nation.
        </p>
        <div className="hero-buttons">
          <button className="btn btn-primary">Explore Rugby Stories</button>
          <button className="btn btn-secondary">Watch Iconic Moments</button>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
