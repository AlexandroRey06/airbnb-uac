/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",  // Esto es importante para que Tailwind sepa qué archivos procesar
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}