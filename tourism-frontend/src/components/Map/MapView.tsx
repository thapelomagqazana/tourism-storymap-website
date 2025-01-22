import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import './MapView.scss';
import { Attraction } from '../../interfaces/Attraction';
import L from 'leaflet';
import trophyIcon from '../../assets/icons/trophy.png'; // Icon for historical events
import playerIcon from '../../assets/icons/rugby.png';
import grassrootsIcon from '../../assets/icons/grassroots.png';

const icons = {
  historical: L.icon({
    iconUrl: trophyIcon,
    iconSize: [30, 40],
    iconAnchor: [15, 40],
    popupAnchor: [0, -40],
  }),
  legend: L.icon({
    iconUrl: playerIcon,
    iconSize: [30, 40],
    iconAnchor: [15, 40],
    popupAnchor: [0, -40],
  }),
  grassroots: L.icon({
    iconUrl: grassrootsIcon,
    iconSize: [30, 40],
    iconAnchor: [15, 40],
    popupAnchor: [0, -40],
  }),
};


interface MapViewProps {
  attractions: Attraction[];
  onMarkerClick: (attraction: Attraction) => void;
}

/**
 * MapView Component
 * Displays an interactive map with attraction markers.
 * @param {Attraction[]} attractions - Array of attraction data.
 * @param {Function} onMarkerClick - Function to handle marker clicks.
 * @returns {JSX.Element} Map with markers and popups.
 */
const MapView: React.FC<MapViewProps> = ({ attractions, onMarkerClick }) => {
  return (
    <div className="map-view">
      <MapContainer
        center={[-28.4793, 24.6727]} // Center on South Africa
        zoom={5}
        className="map"
      >
        {/* Map Tiles */}
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />

        {/* Render Markers */}
        {attractions.map((attraction) => (
          <Marker
            key={attraction.id}
            position={attraction.coordinates}
            icon={icons[attraction.type]} // Use the corresponding icon
            eventHandlers={{
              click: () => onMarkerClick(attraction),
            }}
          >
            <Popup>
              <strong>{attraction.name}</strong>
              <p>{attraction.description}</p>
            </Popup>
          </Marker>

        ))}
      </MapContainer>
    </div>
  );
};

export default MapView;
