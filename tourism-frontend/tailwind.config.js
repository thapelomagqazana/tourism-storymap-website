/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'], // Paths to all templates
  theme: {
    extend: {
      colors: {
        primary: '#1D4ED8', // Example: Primary theme color
        secondary: '#9333EA', // Example: Secondary theme color
        accent: '#F59E0B', // Example: Accent color for highlights
        neutral: '#374151', // Neutral gray for backgrounds
      },
      spacing: {
        '128': '32rem', // Custom spacing value
        '144': '36rem', // Custom spacing value
      },
      fontFamily: {
        sans: ['Inter', 'Arial', 'sans-serif'], // Custom sans-serif font
        serif: ['Merriweather', 'serif'], // Custom serif font
      },
      borderRadius: {
        xl: '1.5rem', // Custom border radius
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'), // Plugin for better form styling
    require('@tailwindcss/typography'), // Plugin for rich text styling
    require('@tailwindcss/aspect-ratio'), // Plugin for maintaining aspect ratios
  ],
};

