import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Navbar.scss';

/**
 * Navbar Component
 * 
 * Renders a responsive navigation bar for the Rugby Story Map website,
 * incorporating Springbok colors and theme.
 * 
 * @returns {JSX.Element} A responsive navigation bar.
 */
const Navbar: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  // Toggles mobile menu open/close
  const toggleMenu = () => setMenuOpen(!menuOpen);

  return (
    <header className="navbar">
      <nav className="container flex justify-between items-center py-4">
        {/* Logo */}
        <div className="navbar-logo text-xl font-headings text-primary">
          <Link to="/">🏉 Rugby: A Nation United</Link>
        </div>

        {/* Hamburger Menu Button (Mobile) */}
        <button
          className="hamburger-menu lg:hidden"
          onClick={toggleMenu}
          aria-label="Toggle menu"
        >
          ☰
        </button>

        {/* Navigation Links */}
        <ul
          className={`navbar-links lg:flex items-center gap-6 transition-all duration-300 ${
            menuOpen ? 'block' : 'hidden'
          }`}
        >
          <li>
            <Link to="/" className="navbar-link">
              Home
            </Link>
          </li>
          <li>
            <Link to="/storyline" className="navbar-link">
              Storyline
            </Link>
          </li>
          <li>
            <Link to="/stadiums" className="navbar-link">
              Stadiums
            </Link>
          </li>
          <li>
            <Link to="/legends" className="navbar-link">
              Legends
            </Link>
          </li>
        </ul>

        {/* Call-to-Action Button */}
        <div className="navbar-cta hidden lg:block">
          <Link to="/trips" className="cta-button bg-secondary text-light px-4 py-2 rounded">
            Plan Your Rugby Trip
          </Link>
        </div>
      </nav>
    </header>
  );
};

export default Navbar;
