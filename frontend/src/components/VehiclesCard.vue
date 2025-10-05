<template>
  <div class="vehicle-card" v-if="vehicle">
    <!-- Close Button -->
    <button class="btn btn-close" @click="$emit('close')">×</button>

    <!-- Vehicle Header -->
    <div class="vehicle-header">
      <div class="header-item">
        <span class="label">Vehicle</span>
        <span class="value">{{ vehicle.vehicle }}</span>
      </div>
      <div class="header-item">
        <span class="label">Type</span>
        <span class="value">{{ vehicle.type }}</span>
      </div>
    </div>

    <!-- Remove Button -->
    <div v-if="projectId" class="remove-btn-container">
      <button @click="removeFromProject" class="btn btn-remove">
        Remove from Project
      </button>
    </div>
  </div>

  <div v-else-if="error" class="error">Failed to load vehicle data</div>
  <div v-else class="loading">Loading...</div>
</template>

<script>
import axios from "axios";

export default {
  name: "VehicleCard",
props: {
  vehicle: { type: Object, required: true },
  projectId: { type: Number, default: null }
},
  data() {
    return {
      error: false
    };
  },
  methods: {
    async removeFromProject() {
      if (!this.projectId || !this.vehicle) return;
      if (!confirm(`Remove ${this.vehicle.vehicle} from this project?`)) return;

      try {
        await axios.post(
          `http://localhost:8000/projects/${this.projectId}/remove-vehicle/${this.vehicle.vehicle_id}`,
          {}
        );
        alert("Vehicle removed from project.");
        this.$emit("removed"); // trigger refresh in parent
        this.$emit("close");   // close the modal
      } catch (err) {
        console.error(err);
        alert("Failed to remove vehicle.");
      }
    }
  },
  emits: ["close", "removed"]
};
</script>

<style scoped>
.vehicle-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  max-width: 600px;
  margin: auto;
  position: relative;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.vehicle-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.header-item {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 0.9em;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.value {
  font-size: 1.1em;
  color: #1f2937;
  font-weight: 500;
}

.btn-remove {
  background-color: #e74c3c;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 16px;
}

.btn-remove:hover {
  background-color: #c0392b;
}

.remove-btn-container {
  text-align: right;
  margin-bottom: 16px;
}

.btn-close {
  position: absolute;
  top: 12px;
  right: 12px;
  background: transparent;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #888;
}

.btn-close:hover {
  color: #333;
}

.loading,
.error {
  text-align: center;
  padding: 20px;
}
</style>
