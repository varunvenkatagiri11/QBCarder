import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/flashcards': 'http://localhost:8080',
      '/auth': 'http://localhost:8080',
      '/saved-flashcards': 'http://localhost:8080'
    }
  }
})
