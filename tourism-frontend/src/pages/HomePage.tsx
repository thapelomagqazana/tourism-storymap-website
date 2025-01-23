import React, { useState } from 'react';
import MapView from '../components/Map/MapView';
import SearchBar from '../components/SearchBar/SearchBar';
import { Attraction } from '../interfaces/Attraction';
import './HomePage.scss';

const attractions: Attraction[] = [
  {
    id: 1,
    name: 'Newlands Stadium',
    description: 'Newlands, the oldest rugby stadium in South Africa, hosted the Springboks’ first match in 1891.',
    entranceFee: 'R100',
    directions: 'Cape Town, Western Cape',
    images: [
      'https://via.placeholder.com/150/0000FF/808080?Text=Newlands1',
      'https://via.placeholder.com/150/0000FF/808080?Text=Newlands2',
      'https://via.placeholder.com/150/0000FF/808080?Text=Newlands3',
    ],
    video: 'https://www.youtube.com/embed/sample-video1',
    coordinates: [-33.9706, 18.4687],
    type: 'historical',
  },
  {
    id: 2,
    name: 'Ellis Park Stadium',
    description: 'Venue of the iconic 1995 Rugby World Cup final, symbolizing hope and reconciliation.',
    entranceFee: 'R150',
    directions: 'Johannesburg, Gauteng',
    images: [
      'https://via.placeholder.com/150/FF0000/FFFFFF?Text=EllisPark1',
      'https://via.placeholder.com/150/FF0000/FFFFFF?Text=EllisPark2',
      'https://via.placeholder.com/150/FF0000/FFFFFF?Text=EllisPark3',
    ],
    video: 'https://www.youtube.com/embed/sample-video2',
    coordinates: [-26.1979, 28.0625],
    type: 'historical',
  },
];

const HomePage: React.FC = () => {
  const [currentSlide, setCurrentSlide] = useState(0);

  const handleNextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % attractions.length);
  };

  const handlePreviousSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + attractions.length) % attractions.length);
  };

  return (
    <div className="homepage">
      <SearchBar attractions={attractions} onSearchResult={() => {}} />
      <div className="slideshow">
        <button className="prev-button" onClick={handlePreviousSlide}>
          ← Previous
        </button>
        <div className="slide-content">
          {/* Slide Title */}
          <h3>{attractions[currentSlide].name}</h3>

          {/* Slide Description */}
          <p>{attractions[currentSlide].description}</p>

          {/* Image Carousel */}
          <div className="image-carousel">
            {attractions[currentSlide].images.map((image, index) => (
              <img key={index} src={image} alt={`${attractions[currentSlide].name} - ${index + 1}`} />
            ))}
          </div>

          {/* Embedded Video */}
          {attractions[currentSlide].video && (
            <iframe
              src={attractions[currentSlide].video}
              title={`${attractions[currentSlide].name} Video`}
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            ></iframe>
          )}

          {/* Map with Highlighted Location */}
          <MapView
            attractions={attractions}
            highlightedAttraction={attractions[currentSlide]} // Highlight current slide location
            onMarkerClick={() => {}}
          />

          {/* Call-to-Action Buttons */}
          <div className="cta-buttons">
            <button className="learn-more-btn">Learn More</button>
            <button className="add-to-trip-btn">Add to Trip</button>
          </div>
        </div>
        <button className="next-button" onClick={handleNextSlide}>
          Next →
        </button>
      </div>
    </div>
  );
};

export default HomePage;
