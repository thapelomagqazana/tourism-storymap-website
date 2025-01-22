import React, { useState } from 'react';
import MapView from '../components/Map/MapView';
import Sidebar from '../components/Sidebar/Sidebar';
import SuggestedTrips from '../components/Trip/SuggestedTrips';
import HeroSection from '../components/HeroSection/HeroSection';
import { Attraction } from '../interfaces/Attraction';

const attractions: Attraction[] = [
  {
    id: 1,
    name: 'Ellis Park Stadium',
    description: 'Venue of the iconic 1995 Rugby World Cup final.',
    entranceFee: 'R150',
    directions: 'Located in Doornfontein, Johannesburg.',
    images: ['https://via.placeholder.com/100'],
    coordinates: [-26.1979, 28.0625],
    type: 'historical', // Updated type
  },
  {
    id: 2,
    name: 'Zwide Township',
    description: 'Hometown of Siya Kolisi, South Africa’s first black rugby captain.',
    entranceFee: 'Free',
    directions: 'Located in Port Elizabeth, Eastern Cape.',
    images: ['https://via.placeholder.com/100'],
    coordinates: [-33.8984, 25.5703],
    type: 'legend', // Updated type
  },
  {
    id: 3,
    name: 'Soweto',
    description: 'A hub for grassroots rugby development and community empowerment.',
    entranceFee: 'Free',
    directions: 'Located southwest of Johannesburg.',
    images: ['https://via.placeholder.com/100'],
    coordinates: [-26.2485, 27.854],
    type: 'grassroots', // Updated type
  },
];

  

const HomePage: React.FC = () => {
    const [selectedAttraction, setSelectedAttraction] = useState<any>(null);
  
    const handleMarkerClick = (attraction: any) => {
      setSelectedAttraction(attraction);
    };
  
    const closeSidebar = () => {
      setSelectedAttraction(null);
    };
  
    const handleAddToTrip = (attraction: any) => {
      alert(`${attraction.name} has been added to your trip!`);
    };
  
    const handleViewMoreDetails = (attraction: any) => {
      alert(`Viewing more details about ${attraction.name}.`);
    };
  
    return (
      <div className="homepage">
        <HeroSection />
        <MapView
          attractions={attractions}
          onMarkerClick={handleMarkerClick}
        />
        {selectedAttraction && (
          <Sidebar
            attraction={selectedAttraction}
            onClose={closeSidebar}
            onAddToTrip={handleAddToTrip}
            onViewMoreDetails={handleViewMoreDetails}
          />
        )}
        {/* <SuggestedTrips /> */}
      </div>
    );
};

export default HomePage;
