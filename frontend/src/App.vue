<template>
  <!-- Header always visible -->
  <div class="header">
    <h1>Project Planner</h1>
    <img src="./images/Projec(2).png" alt="Your Logo" class="header-logo">
  </div>

  <!-- Navbar only visible when authenticated -->
  <nav v-if="isAuthenticated && $route.path !== '/login'" class="navbar">
    <!-- Hamburger (visible on mobile) -->
    <button class="navbar-toggle" @click="toggleMenu">
      <span></span>
      <span></span>
      <span></span>
    </button>

    <!-- Menu items -->
    <ul :class="['navbar-menu', { 'is-active': isMenuOpen }]">
      <li><router-link to='/project_dashboard' class="nav-link">Project Dashboard</router-link></li>
      <li><router-link to='/personel_dashboard' class="nav-link">Personel Dashboard</router-link></li>
      <li><router-link to='/vehicles_dashboard' class="nav-link">Vehicles Dashboard</router-link></li>
      <li><router-link to='/project_calendar' class="nav-link">Project Calendar</router-link></li>
      <li><router-link to='/personel_calendar' class="nav-link">Personel Calendar</router-link></li>
      <li><router-link to='/vehicles_calendar' class="nav-link">Vehicles Calendar</router-link></li>
      <li class="logout-item"><button @click="logout" class="logout-btn">Logout</button></li>
      <li><router-link to='/Try2' class="nav-link">TryPage2</router-link></li>
      <li><router-link to='/Try' class="nav-link">TryPage</router-link></li>
    </ul>
  </nav>

  <router-view></router-view>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
      isMenuOpen: false,
      // Initialize directly from localStorage
      isAuthenticated: localStorage.getItem('isAuthenticated') === 'true'
    };
  },
  methods: {
    checkAuth() {
      this.isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
    },
    logout() {
      localStorage.removeItem('isAuthenticated');
      this.isAuthenticated = false;
      this.$router.push('/login');
    },
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
    }
  },
  mounted() {
    window.addEventListener('storage', this.checkAuth);
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.checkAuth);
  },
  watch: {
    '$route'() {
      this.checkAuth();
      this.isMenuOpen = false;
    }
  }
}
</script>

<style scoped>
/* Header Styles - White and Simple */
.header {
  padding: 15px 20px;
  text-align: center;
  background: #ffffff;
  color: #1f2937;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header h1 {
  margin: 0 0 10px 0;
  font-size: 2rem;
  font-weight: 600;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #1f2937;
}

.header-logo {
  width: 120px;
  height: auto;
  display: block;
  margin: 0 auto;
  /* Removed the filter that was making it invisible */
}

/* Navbar Styles */
.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 0;
  transition: all 0.3s ease;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 0;
  list-style: none;
  margin: 0;
  padding: 0;
  height: 60px;
}

/* Navigation Links */
.nav-link {
  text-decoration: none;
  color: #374151;
  font-weight: 500;
  padding: 18px 20px;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  align-items: center;
  height: 100%;
  border-bottom: 3px solid transparent;
}

.nav-link:hover {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  color: #2563eb;
  transform: translateY(-1px);
}

.nav-link.router-link-active {
  color: #2563eb;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-bottom: 3px solid #2563eb;
  font-weight: 600;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  background: #2563eb;
  border-radius: 50%;
}

/* Logout Button */
.logout-item {
  margin-left: auto;
  padding: 0 20px;
}

.logout-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  box-shadow: 0 2px 10px rgba(220, 38, 38, 0.2);
}

.logout-btn:hover {
  background: linear-gradient(135deg, #5b1cb9, #34045e);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(220, 38, 38, 0.3);
}

.logout-btn:active {
  transform: translateY(0);
}

/* Hamburger Menu */
.navbar-toggle {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  width: 30px;
  height: 21px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin: 20px;
}

.navbar-toggle span {
  display: block;
  height: 3px;
  width: 100%;
  background: #374151;
  border-radius: 3px;
  transition: all 0.3s ease;
  transform-origin: center;
}

.navbar-toggle:hover span {
  background: #2563eb;
}

.navbar-toggle.is-active span:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
}

.navbar-toggle.is-active span:nth-child(2) {
  opacity: 0;
}

.navbar-toggle.is-active span:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
}

/* Responsive Design */
@media (max-width: 768px) {
  .navbar-toggle {
    display: flex;
  }

  .navbar-menu {
    display: none;
    flex-direction: column;
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    padding: 0;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
    border-radius: 0 0 12px 12px;
    height: auto;
  }

  .navbar-menu.is-active {
    display: flex;
  }

  .nav-link {
    padding: 15px 20px;
    border-bottom: 1px solid #f3f4f6;
    border-left: 4px solid transparent;
    height: auto;
  }

  .nav-link.router-link-active {
    border-left: 4px solid #2563eb;
    border-bottom: 1px solid #f3f4f6;
  }

  .nav-link.router-link-active::after {
    display: none;
  }

  .logout-item {
    margin-left: 0;
    padding: 15px 20px;
    border-top: 1px solid #f3f4f6;
  }

  .logout-btn {
    width: 100%;
    margin: 10px 0;
  }
}

/* Smooth transitions for all interactive elements */
.navbar-menu li {
  transition: transform 0.2s ease;
}

.navbar-menu li:active {
  transform: scale(0.98);
}

/* App container spacing */
#app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin-top: 0;
}
</style>