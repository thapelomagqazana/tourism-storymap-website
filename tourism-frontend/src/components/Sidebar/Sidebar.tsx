import React from 'react';
import './Sidebar.scss';
import { Attraction } from '../../interfaces/Attraction';

interface SidebarProps {
  attraction: Attraction;
  onClose: () => void;
  onAddToTrip: (attraction: Attraction) => void;
  onViewMoreDetails: (attraction: Attraction) => void;
}

/**
 * Sidebar Component
 * @description Displays detailed information about a selected attraction.
 * @param {Attraction} attraction - The selected attraction data.
 * @param {Function} onClose - Function to close the sidebar.
 * @param {Function} onAddToTrip - Function to add the attraction to a trip.
 * @param {Function} onViewMoreDetails - Function to view more details about the attraction.
 * @returns {JSX.Element} The sidebar component.
 */
const Sidebar: React.FC<SidebarProps> = ({
  attraction,
  onClose,
  onAddToTrip,
  onViewMoreDetails,
}) => {
  return (
    <div className="sidebar">
      <button className="sidebar-close" onClick={onClose} aria-label="Close Sidebar">
        &times;
      </button>

      <div className="sidebar-content">
        <h2 className="sidebar-title">{attraction.name}</h2>
        <p className="sidebar-description">{attraction.description}</p>
        <div className="sidebar-images">
          {attraction.images.map((image, index) => (
            <img
              key={index}
              src={image}
              alt={`${attraction.name} view ${index + 1}`}
              className="sidebar-image"
            />
          ))}
        </div>
        <div className="sidebar-info">
          <p>
            <strong>Entrance Fee:</strong> {attraction.entranceFee}
          </p>
          <p>
            <strong>Directions:</strong> {attraction.directions}
          </p>
        </div>
        <div className="sidebar-buttons">
          <button
            className="btn btn-primary"
            onClick={() => onAddToTrip(attraction)}
          >
            Add to Trip
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => onViewMoreDetails(attraction)}
          >
            View More Details
          </button>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
