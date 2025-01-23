import React, { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import './MapView.scss';
import L from 'leaflet';
import { Attraction } from '../../interfaces/Attraction';

// Custom marker icon
const customIcon = new L.Icon({
  iconUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Map_marker.svg/585px-Map_marker.svg.png?20150513095621',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

interface MapViewProps {
  attractions: Attraction[];
  highlightedAttraction?: Attraction | null; // Optional highlighted attraction
  onMarkerClick: (attraction: Attraction) => void;
}

// Component to dynamically fly the map to a new location
const FlyToLocation: React.FC<{ center: [number, number]; zoom: number }> = ({ center, zoom }) => {
  const map = useMap();

  useEffect(() => {
    map.flyTo(center, zoom, {
      duration: 1.5, // Animation duration in seconds
      easeLinearity: 0.25, // Easing function for the animation
    });
  }, [center, zoom, map]);

  return null;
};

const MapView: React.FC<MapViewProps> = ({ attractions, highlightedAttraction, onMarkerClick }) => {
  return (
    <div className="map-view">
      <MapContainer
        center={highlightedAttraction?.coordinates || [-30.5595, 22.9375]} // Default to South Africa center
        zoom={highlightedAttraction ? 10 : 5} // Default zoom
        scrollWheelZoom={true}
        style={{ width: '100%', height: '400px' }}
      >
        {/* Map Tiles */}
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />

        {/* Dynamic Fly-To Animation */}
        {highlightedAttraction && (
          <FlyToLocation
            center={highlightedAttraction.coordinates}
            zoom={10}
          />
        )}

        {/* Highlighted Marker (Pin) */}
        {highlightedAttraction && (
          <Marker position={highlightedAttraction.coordinates} icon={customIcon}>
            <Popup>
              <h3>{highlightedAttraction.name}</h3>
              <p>{highlightedAttraction.description}</p>
            </Popup>
          </Marker>
        )}

        {/* All Markers */}
        {attractions.map((attraction) => (
          <Marker
            key={attraction.id}
            position={attraction.coordinates}
            icon={customIcon}
            eventHandlers={{
              click: () => {
                onMarkerClick(attraction);
              },
            }}
          >
            <Popup>
              <h3>{attraction.name}</h3>
              <p>{attraction.description}</p>
              <button
                onClick={() => {
                  onMarkerClick(attraction);
                }}
              >
                Learn More
              </button>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
};

export default MapView;
