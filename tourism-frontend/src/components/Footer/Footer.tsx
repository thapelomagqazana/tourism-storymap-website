import React from 'react';
import './Footer.scss';
import { FaFacebook, FaTwitter, FaInstagram } from 'react-icons/fa';

/**
 * Footer Component
 *
 * @description
 * Displays social media links and quick links in the footer of the website.
 *
 * @returns {JSX.Element} Footer component.
 */
const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="container">
        {/* Social Links */}
        <div className="social-links">
          <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" aria-label="Facebook">
            <FaFacebook />
          </a>
          <a href="https://twitter.com" target="_blank" rel="noopener noreferrer" aria-label="Twitter">
            <FaTwitter />
          </a>
          <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" aria-label="Instagram">
            <FaInstagram />
          </a>
        </div>

        {/* Quick Links */}
        <ul className="quick-links">
          <li>
            <a href="#">Privacy Policy</a>
          </li>
          <li>
            <a href="#">Terms of Service</a>
          </li>
          <li>
            <a href="/contact-us">Contact Us</a>
          </li>
        </ul>

        {/* Copyright */}
        <p className="copyright">
          &copy; {new Date().getFullYear()} Rugby: A Nation United. All rights reserved.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
