<template>
  <div class="project-card">
    <!-- <button class="close-btn" @click="closeForm">×</button> -->
    <h3>Edit Project: {{ form?.project_name }}</h3>

    <form v-if="form" @submit.prevent="updateProject" class="project-form">
      <div class="form-row">
        <div class="form-group">
          <label>Project Code</label>
          <input v-model="form.project_code" type="text" required />
        </div>
        <div class="form-group">
          <label>Project Name</label>
          <input v-model="form.project_name" type="text" required />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Location</label>
          <input v-model="form.location" type="text" />
        </div>
        <div class="form-group">
          <label>Duration (days)</label>
          <input v-model.number="form.duration" type="number" min="1" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Expected Personnel</label>
          <input v-model.number="form.expected_personel" type="number" min="0" />
        </div>
        <div class="form-group">
          <label>Crane Required</label>
          <select v-model="form.crane">
            <option value="No">No</option>
            <option value="Yes">Yes</option>
          </select>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Coordinates (Lat, Lng)</label>
          <input v-model="form.xy_map" type="text" placeholder="e.g., 40.7128, -74.0060" />
        </div>
        <div class="form-group">
          <label>Start Date</label>
          <input v-model="form.date_start" type="date" />
        </div>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn btn-submit" :disabled="saving">
          {{ saving ? "Saving..." : "Save" }}
        </button>
      </div>
    </form>

    <div v-else class="loading">Loading project data...</div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ProjectCard",
  props: {
    project: { type: Object, required: true }
  },
  data() {
    return {
      form: null,
      saving: false
    };
  },
  watch: {
    project: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.form = { ...newVal }; // copy prop to form
        }
      }
    }
  },
  methods: {
    async updateProject() {
      try {
        this.saving = true;
        await axios.patch(
          `http://localhost:8000/projects/${this.form.project_id}/update`,
          this.form
        );
        alert("Project updated!");
        this.$emit("updated");
      } catch (err) {
        console.error(err);
        alert("Failed to update project");
      } finally {
        this.saving = false;
      }
    }
    ,
    // Add this method
    closeForm() {
      this.$emit('close');
    }
  }
};
</script>

<style scoped>
.project-card {
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  max-width: 600px;
  width: 90%;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.project-card h3 {
  color: #2d3748;
  margin-bottom: 20px;
}

.project-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 6px;
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.form-group input,
.form-group select {
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  background: white;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group input:required:invalid {
  border-color: #ef4444;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #e5e7eb;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.project-card h3 {
  color: #2d3748;
  margin-bottom: 20px;
  padding-right: 40px; /* Add padding to prevent overlap with close button */
}

.btn-submit {
  background: #667eea;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-submit:hover:not(:disabled) {
  background: #5a67d8;
  transform: translateY(-1px);
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>
