import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  // Load environment variables based on the current mode
  const env = loadEnv(mode, process.cwd(), 'VITE_');

  return {
    server: {
      port: parseInt(env.VITE_PORT || '3000'),
      open: true,
      proxy: {
        '/api': {
          target: env.VITE_API_URL || 'https://api.example.com',
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
      watch: {
        ignored: ['node_modules/**', 'dist/**', '.git/**', 'public/**'],
        usePolling: true,
        interval: 100,
      },
    },
    build: {
      outDir: 'dist', // Directory for the production build
      sourcemap: false, // Disable source maps in production for smaller build size
      minify: 'esbuild', // Use esbuild for fast and efficient minification
      rollupOptions: {
        output: {
          manualChunks(id) {
            // Split dependencies into separate chunks
            if (id.includes('node_modules')) {
              if (id.includes('react')) {
                return 'react-vendor';
              }
              return 'vendor';
            }
          },
        },
      },
      chunkSizeWarningLimit: 500, // Increase chunk size warning limit
    },
    resolve: {
      alias: {
        '@': '/src',
      },
    },
    plugins: [react()],
    envPrefix: 'VITE_',
    optimizeDeps: {
      include: ['react', 'react-dom'], // Pre-bundle these dependencies for faster builds
    },
  };
});
