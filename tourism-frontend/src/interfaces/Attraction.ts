export interface Attraction {
    id: number;
    name: string;
    description: string;
    entranceFee: string;
    directions: string;
    images: string[];
    coordinates: [number, number];
    type: 'legend' | 'historical' | 'grassroots'; // Define types
  }
  