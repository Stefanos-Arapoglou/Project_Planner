<template>
  <div class="header">
    <h1>Project Planner</h1>
    <img src="./images/Projec(2).png" alt="Your Logo" class="header-logo">
  </div>
  <nav class="navbar">
    <!-- Logo in the center -->


    <!-- Hamburger (visible on mobile) -->
    <button class="navbar-toggle" @click="toggleMenu">
      ☰
    </button>

    <!-- Menu items -->
    <ul :class="['navbar-menu', { 'is-active': isMenuOpen }]">
      <li><router-link to ='/project_dashboard'> Project Dashboard </router-link></li>
      <li><router-link to ='/personel_dashboard'> Personel Dashboard </router-link></li>
      <li><router-link to ='/vehicles_dashboard'> Vehicles Dashboard </router-link></li>
      <li><router-link to ='/project_calendar'> Project Calendar </router-link></li>
      <li><router-link to ='/personel_calendar'> Personel Calendar </router-link></li>
      <li><router-link to ='/vehicles_calendar'> Vehicles Calendar </router-link></li>
      
      <router-link to ='/world'> Go to world </router-link>
      <router-link to ='/try'> Go to try page </router-link>
      <router-link to ='/try2'> Go to try2 page </router-link>
    </ul>
  </nav>

<router-view></router-view>
</template>

<script>

export default {
  name: "App",
  data() {
    return {
    };
  },


  methods: {

  },
  components: {  },
};


</script>
<style>

  /* Project Info Specific Modal Styles */

.header {
  padding: 5px 10px; /* Reduced padding to make space smaller */
  text-align: center;
}

.header h1 {
  margin: 0 0 5px 0; /* Remove top/bottom margin, add small bottom margin */
}

.header-logo {
  width: 150px; /* Adjust size as needed */
  height: auto;
  display: block;
  margin: 0 auto; /* Center the logo below the title */
}


#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
button {
       padding : 10px;
      background: lightblue;
}

/* Base navbar layout */
.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 1rem 0;
  transition: all 0.3s ease;
}

/* Logo */
.navbar-logo {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 1.25rem;
  font-weight: bold;
}

/* Menu */
.navbar-menu {
  display: flex;
  gap: 1.5rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.navbar-menu li a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
}

.navbar-menu li a:hover {
  color: #007bff;
}

/* Toggle (hidden on desktop) */
.navbar-toggle {
  display: none;
  font-size: 1.5rem;
  background: none;
  border: none;
  cursor: pointer;
}

/* Responsive behavior */
@media (max-width: 768px) {
  .navbar-menu {
    display: none;
    flex-direction: column;
    position: absolute;
    top: 100%;
    right: 2rem;
    background: white;
    padding: 1rem;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    border-radius: 0.5rem;
  }

  .navbar-menu.is-active {
    display: flex;
  }

  .navbar-toggle {
    display: block;
  }


}
</style>