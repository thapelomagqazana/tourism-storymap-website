import React from 'react';
import './SuggestedTrips.scss';

interface Trip {
  id: number;
  title: string;
  days: number;
  description: string;
  attractions: string[];
}

const trips: Trip[] = [
  {
    id: 1,
    title: '3-Day Adventure in Cape Town',
    days: 3,
    description: 'Explore the vibrant city of Cape Town with this 3-day itinerary.',
    attractions: ['Table Mountain', 'Robben Island', 'Cape Winelands'],
  },
  {
    id: 2,
    title: '5-Day Wildlife Safari',
    days: 5,
    description: 'Experience the best of South African wildlife with this 5-day trip.',
    attractions: ['Kruger National Park', 'Blyde River Canyon', 'God’s Window'],
  },
];

const SuggestedTrips: React.FC = () => {
  return (
    <section className="suggested-trips">
      <h2 className="section-title">Suggested Trips</h2>
      <div className="trips-container">
        {trips.map((trip) => (
          <div className="trip-card" key={trip.id}>
            <h3 className="trip-title">{trip.title}</h3>
            <p className="trip-description">{trip.description}</p>
            <ul className="trip-attractions">
              {trip.attractions.map((attraction, index) => (
                <li key={index}>{attraction}</li>
              ))}
            </ul>
            <div className="trip-actions">
              <button className="btn view-details">View Details</button>
              <button className="btn customize-trip">Customize Trip</button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default SuggestedTrips;
