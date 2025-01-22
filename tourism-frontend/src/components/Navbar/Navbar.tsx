import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Navbar.scss';

/**
 * Navbar Component
 * 
 * Renders a responsive navigation bar for the Rugby Story Map website.
 * 
 * @returns {JSX.Element} A responsive navigation bar.
 */
const Navbar: React.FC = () => {
  // State to manage mobile menu visibility
  const [menuOpen, setMenuOpen] = useState(false);

  /**
   * Toggles the mobile menu open/close state.
   */
  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <header className="navbar">
      <nav className="container flex justify-between items-center py-4">
        {/* Logo */}
        <div className="navbar-logo text-xl font-headings text-primary">
          <Link to="/">🏉 Rugby: A Nation United</Link>
        </div>

        {/* Hamburger Menu Button */}
        <button
          className="hamburger-menu lg:hidden"
          onClick={toggleMenu}
          aria-label="Toggle menu"
        >
          ☰
        </button>

        {/* Navigation Links */}
        <ul
          className={`navbar-links transition-all duration-300 lg:flex ${
            menuOpen ? 'block' : 'hidden'
          }`}
          aria-expanded={menuOpen}
        >
          <li>
            <Link to="/" className="navbar-link">
              Home
            </Link>
          </li>
          <li>
            <Link to="/legends" className="navbar-link">
              Legends
            </Link>
          </li>
          <li>
            <Link to="/grassroots" className="navbar-link">
              Grassroots
            </Link>
          </li>
          <li>
            <Link to="/trips" className="navbar-link">
              Trips
            </Link>
          </li>
          <li>
            <Link to="/contact" className="navbar-link">
              Contact Us
            </Link>
          </li>
        </ul>

        {/* Call-to-Action Button */}
        <div className="navbar-cta hidden lg:block">
          <Link to="/trips" className="cta-button">
            Plan Your Rugby Trip
          </Link>
        </div>
      </nav>
    </header>
  );
};

export default Navbar;
