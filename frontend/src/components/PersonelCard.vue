<template>
  <div class="personel-card" v-if="personel">
    <!-- Close Button -->
    <button class="btn btn-close" @click="$emit('close')">×</button>

<div class="personel-header">
  <div class="header-item">
    <span class="label">Name</span>
    <span class="value">{{ personel.personel_name }}</span>
  </div>
  <div class="header-item">
    <span class="label">Surname</span>
    <span class="value">{{ personel.personel_surname }}</span>
  </div>
</div>

    <p><strong>Role:</strong> {{ personel.role }}</p>

    <!-- Remove Button -->
    <div v-if="projectId" class="remove-btn-container">
      <button @click="removeFromProject" class="btn btn-remove">
        Remove from Project
      </button>
    </div>

    <!-- Educations -->
    <h4>Educations</h4>
    <table class="info-table">
      <thead>
        <tr>
          <th>Date Taken</th>
          <th>First Time</th>
          <th>Days Until Expiration</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="edu in personel.education || []" :key="edu.education_id">
          <td>{{ formatDate(edu.education_date) }}</td>
          <td>{{ edu.first_time }}</td>
          <td>{{ daysUntilExpiration(edu.education_date, 3) }}</td>
        </tr>
        <tr v-if="!personel.education || personel.education.length === 0">
          <td colspan="3">No educations</td>
        </tr>
      </tbody>
    </table>

    <!-- Medical Exams -->
    <h4>Medical Exams</h4>
    <table class="info-table">
      <thead>
        <tr>
          <th>Date</th>
          <th>Expiration</th>
          <th>Days Until Expiration</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="exam in personel.medicals || []" :key="exam.exams_id">
          <td>{{ formatDate(exam.exam_date) }}</td>
          <td>{{ formatDate(addYears(exam.exam_date, 1)) }}</td>
          <td>{{ daysUntilExpiration(exam.exam_date, 1) }}</td>
        </tr>
        <tr v-if="!personel.medicals || personel.medicals.length === 0">
          <td colspan="3">No medical exams</td>
        </tr>
      </tbody>
    </table>

    <!-- X-Rays -->
    <h4>X-Rays</h4>
    <table class="info-table">
      <thead>
        <tr>
          <th>Date</th>
          <th>Expiration</th>
          <th>Days Until Expiration</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="xray in personel.xrays || []" :key="xray.xray_id">
          <td>{{ formatDate(xray.xrays_date) }}</td>
          <td>{{ formatDate(addYears(xray.xrays_date, 2)) }}</td>
          <td>{{ daysUntilExpiration(xray.xrays_date, 2) }}</td>
        </tr>
        <tr v-if="!personel.xrays || personel.xrays.length === 0">
          <td colspan="3">No X-Rays</td>
        </tr>
      </tbody>
    </table>
  </div>

  <div v-else-if="error" class="error">Failed to load personel data</div>
  <div v-else class="loading">Loading...</div>
</template>

<script>
import axios from "axios";

export default {
  name: "PersonelCard",
  props: {
    personelId: { type: Number, required: true },
    projectId: { type: Number, default: null }
  },
  data() {
    return {
      personel: null,
      error: false
    };
  },
  async mounted() {
    try {
      const res = await axios.get(`http://localhost:8000/personel/${this.personelId}`);
      this.personel = res.data;
    } catch (err) {
      console.error(err);
      this.error = true;
    }
  },
  methods: {
    formatDate(date) {
      if (!date) return "N/A";
      return new Date(date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric"
      });
    },
    addYears(date, years) {
      if (!date) return null;
      const d = new Date(date);
      d.setFullYear(d.getFullYear() + years);
      return d;
    },
    daysUntilExpiration(date, yearsValid) {
      if (!date) return "-";
      const expiration = this.addYears(date, yearsValid);
      const today = new Date();
      const diffTime = expiration - today;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays >= 0 ? diffDays : "Expired";
    },
    async removeFromProject() {
      if (!this.projectId || !this.personel) return;
      if (!confirm(`Remove ${this.personel.personel_name} from project?`)) return;

      try {
        await axios.post(
          `http://localhost:8000/projects/${this.projectId}/remove-personel/${this.personel.personel_id}`,
          {}
        );
        alert("Personel removed from project.");
        this.$emit("removed");
        this.$emit("close");
      } catch (err) {
        console.error(err);
        alert("Failed to remove personel.");
      }
    }
  },
  emits: ['close', 'removed']
};
</script>

<style scoped>
.personel-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  max-width: 800px;
  margin: auto;
  position: relative;
}

.personel-title {
  font-size: 1.25rem;
  margin-bottom: 12px;
  font-weight: 600;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.info-table th,
.info-table td {
  padding: 8px 12px;
  border: 1px solid #ddd;
  text-align: left;
}

.info-table th {
  background: #f0f0f0;
  font-weight: 600;
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

.loading, .error {
  text-align: center;
  padding: 20px;
}

.personel-header {
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
</style>