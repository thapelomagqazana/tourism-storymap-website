import React, { useState } from 'react';
import './SearchBar.scss';

interface SearchBarProps {
  attractions: any[];
  onSearchResult: (result: any[]) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ attractions, onSearchResult }) => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);

    // Filter attractions by name or description
    const results = attractions.filter(
      (attraction) =>
        attraction.name.toLowerCase().includes(query) ||
        attraction.description.toLowerCase().includes(query)
    );

    onSearchResult(results);
  };

  return (
    <div className="search-bar">
      <input
        type="text"
        placeholder="Search for attractions or events..."
        value={searchQuery}
        onChange={handleSearch}
        className="search-input"
        aria-label="Search Attractions"
      />
    </div>
  );
};

export default SearchBar;
