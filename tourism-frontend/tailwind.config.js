module.exports = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#006341', // Springbok Green
        secondary: '#FFB612', // Golden Yellow
        danger: '#D60000', // Red
        background: '#F4F4F2', // Neutral Background
        accent: '#003DA5', // Blue for depth
        dark: '#000000', // Black
        light: '#FFFFFF', // White
        textMuted: '#b0b0b0', // Muted gray for placeholder text
      },
      fontFamily: {
        headings: ['Playfair Display', 'serif'], // Heritage-focused
        body: ['Inter', 'sans-serif'], // Clean and readable
      },
      backgroundImage: {
        'rugby-pattern': "url('/src/assets/images/rugby-pattern.jpg')", // Custom background
      },
    },
  },
  plugins: [],
};
