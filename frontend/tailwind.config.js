/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#409EFF',
          dark: '#337ecc'
        }
      },
      fontFamily: {
        sans: ['Microsoft YaHei', 'sans-serif']
      }
    },
  },
  plugins: [],
}
