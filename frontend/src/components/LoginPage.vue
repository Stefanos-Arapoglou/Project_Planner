<template>
  <div class="login-container">
    <div class="login-card">
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label>Username</label>
          <input v-model="username" type="text" placeholder="Enter username" required />
        </div>
        <div class="input-group">
          <label>Password</label>
          <input v-model="password" type="password" placeholder="Enter password" required />
        </div>
        <button type="submit" class="login-button">Login</button>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    }
  },
  methods: {
    async handleLogin() {
      try {
        const response = await axios.get('http://localhost:8000/auth', {
          params: {
            username: this.username,
            password: this.password
          }
        });
        if (response.data === true) {
          localStorage.setItem('isAuthenticated', 'true');
          this.$router.push('/project_dashboard');
        } else {
          this.errorMessage = 'Invalid credentials';
        }
      } catch (error) {
        this.errorMessage = 'Error connecting to server';
        console.error(error);
      }
    }
  }
}
</script>

<style scoped>
/* Background and Container */
.login-container {
  display: flex;
  justify-content: center;
  align-items: flex-start; /* Changed from center to flex-start */
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea, #764ba2);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 20px;
}

/* Card */
.login-card {
  background: #ffffff;
  padding: 20px 15px;
  border-radius: 15px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
  width: 70%;
  max-width: 300px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
}

h2 {
  text-align: center;
  margin-bottom: 15px;
  color: #111827;
}

/* Input Groups */
.input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

label {
  margin-bottom: 8px;
  color: #374151;
  font-weight: 500;
}

input {
  padding: 12px 15px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  transition: all 0.3s ease;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 8px rgba(37, 99, 235, 0.3);
}

/* Button */
.login-button {
  width: 100%;
  padding: 12px;
  background: #2563eb;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.3s ease, transform 0.2s ease;
}

.login-button:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
}

/* Error Message */
.error-message {
  margin-top: 15px;
  color: #dc2626;
  font-weight: 500;
  text-align: center;
  animation: shake 0.3s ease;
}

/* Shake animation for errors */
@keyframes shake {
  0% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  50% { transform: translateX(5px); }
  75% { transform: translateX(-5px); }
  100% { transform: translateX(0); }
}
</style>
