import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// import path from 'path';

export default defineConfig({
  // Define server-specific configurations
  server: {
    port: parseInt(process.env.VITE_PORT || '3000'), // Specify the development server port
    open: true, // Automatically open the app in the default browser
    proxy: {
      '/api': {
        target: process.env.VITE_API_URL,
        changeOrigin: true,
        secure: false, // Set to true if using HTTPS and need SSL verification
        rewrite: (path) => path.replace(/^\/api/, ''), // Remove '/api' prefix
      },
    },
  },

  // Define build-specific configurations
  build: {
    outDir: 'dist', // Directory for the production build
    sourcemap: true, // Generate source maps for debugging
    rollupOptions: {
      output: {
        manualChunks: {
          react: ['react', 'react-dom'], // Split React and React-DOM into separate chunks
        },
      },
    },
  },

  // Resolve custom paths for imports
  // resolve: {
  //   alias: {
  //     '@': path.resolve(__dirname, './src'), // Shortcut for 'src' directory
  //     '@api': path.resolve(__dirname, './src/api'),
  //     '@assets': path.resolve(__dirname, './src/assets'),
  //     '@components': path.resolve(__dirname, './src/components'),
  //     '@context': path.resolve(__dirname, './src/context'),
  //     '@interfaces': path.resolve(__dirname, './src/interfaces'),
  //     '@pages': path.resolve(__dirname, './src/pages'),
  //     '@utils': path.resolve(__dirname, './src/utils'),
  //   },
  // },

  // Plugins to enhance Vite functionality
  plugins: [
    react(), // Enables React Fast Refresh and JSX support
  ],

  // Define environment variables prefix
  envPrefix: 'VITE_', // Prefix for environment variables (e.g., VITE_API_URL)
});
