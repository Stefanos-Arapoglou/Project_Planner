<template>
  <div class="vehicles-page">
    <!-- Header Section -->
    <div class="dashboard-header">
      <h1>Vehicles Management</h1>
    </div>

    <!-- Controls Section -->
    <div class="controls">
      <div class="search-box">
        <input type="text" v-model="filters.search" placeholder="Search by vehicle name or type...">
      </div>
      <div class="filter-controls">
        <select v-model="filters.type">
          <option value="">All Types</option>
          <option v-for="type in vehicleTypes" :key="type" :value="type">
            {{ type }}
          </option>
        </select>
        <button class="btn btn-create" @click="showCreateModal = true">
          + Create New Vehicle
        </button>
      </div>
    </div>

    <!-- Main Table -->
    <div class="table-container">
      <table v-if="filteredVehicles.length > 0" class="bordered-table excel-style-table">
        <thead>
          <tr>
            <th class="col-vehicle">Vehicle</th>
            <th class="col-type">Type</th>
            <th class="col-projects">Projects Count</th>
            <th class="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="vehicle in filteredVehicles" :key="vehicle.vehicle_id" class="table-row">
            <td class="col-vehicle cell-bordered">
              <strong>{{ vehicle.vehicle }}</strong>
            </td>
            <td class="col-type cell-bordered">
              <div :class="['type-cell', getTypeClass(vehicle.type)]">
                <span class="type-text">{{ vehicle.type }}</span>
              </div>
            </td>
            <td class="col-projects cell-bordered">
              <div class="projects-cell clickable" @click="openProjectsModal(vehicle)">
                <span class="projects-count">{{ getProjectsCount(vehicle) }}</span>
                <span class="projects-text">projects</span>
              </div>
            </td>
            <td class="col-actions cell-bordered">
              <div class="action-buttons">
                <button class="btn btn-edit" @click="editVehicle(vehicle)">
                  Edit
                </button>
                <button class="btn btn-remove" @click="removeVehicle(vehicle)">
                  Remove
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty-state">
        <div class="empty-icon">🚗</div>
        <h3>No vehicles found</h3>
        <p>Try adjusting your search or filters</p>
      </div>
    </div>

    <!-- Create Vehicle Modal -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal-content create-modal" @click.stop>
        <h3>Create New Vehicle</h3>
        <form @submit.prevent="createVehicle" class="create-form">
          <div class="form-row">
            <div class="form-group">
              <label for="vehicle">Vehicle Name *</label>
              <input type="text" id="vehicle" v-model="newVehicle.vehicle" required placeholder="Enter vehicle name">
            </div>
            <div class="form-group">
              <label for="type">Type *</label>
              <select id="type" v-model="newVehicle.type" required>
                <option value="">Select Type</option>
                <option value="IX">IX</option>
                <option value="Transit">Transit</option>
                <option value="Crane">Crane</option>
              </select>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModals" class="btn btn-cancel">
              Cancel
            </button>
            <button type="submit" class="btn btn-submit" :disabled="creating">
              {{ creating ? 'Creating...' : 'Create Vehicle' }}
            </button>
          </div>
        </form>
      </div>
    </div>

  <!-- Replace the Edit Vehicle Modal section in VehiclesDash.vue -->
<div v-if="showEditModal && selectedVehicle" class="modal-overlay">
  <div class="modal-content create-modal" @click.stop>
    <h3>Edit Vehicle - {{ selectedVehicle.vehicle }}</h3>
    <form @submit.prevent="updateVehicle" class="create-form">
      <div class="form-row">
        <div class="form-group">
          <label for="edit_vehicle">Vehicle Name *</label>
          <input 
            type="text" 
            id="edit_vehicle" 
            v-model="editVehicleData.vehicle" 
            required
            placeholder="Enter vehicle name"
          >
        </div>
        <div class="form-group">
          <label for="edit_type">Type *</label>
          <select id="edit_type" v-model="editVehicleData.type" required>
            <option value="">Select Type</option>
            <option value="IX">IX</option>
            <option value="Transit">Transit</option>
            <option value="Crane">Crane</option>
          </select>
        </div>
      </div>

      <div class="edit-vehicle-actions compact">
        <div class="edit-vehicle-actions-left">
          <button type="button" class="btn btn-danger" @click="deleteVehicle" :disabled="deleting">
            {{ deleting ? 'Deleting...' : 'Delete' }}
          </button>
        </div>
        <div class="edit-vehicle-actions-right">
          <button type="button" @click="closeModals" class="btn btn-cancel">
            Cancel
          </button>
          <button type="submit" class="btn btn-submit" :disabled="updating">
            {{ updating ? 'Updating...' : 'Update' }}
          </button>
        </div>
      </div>
    </form>
  </div>
</div>

    <!-- Projects Modal -->
    <div v-if="showProjectsModal && selectedVehicle" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Projects for {{ selectedVehicle.vehicle }}</h3>
        <div class="records-list">
          <div v-for="project in (selectedVehicle.projects || [])" :key="project.project_id" class="record-item">
            <div class="record-info">
              <strong>Project Code:</strong> {{ project.project_code || 'N/A' }}<br>
              <strong>Project Name:</strong> {{ project.project_name || 'N/A' }}<br>
              <strong>Duration (d):</strong> {{ project.duration || 'N/A' }}
            </div>
          </div>
          <div v-if="!selectedVehicle.projects || selectedVehicle.projects.length === 0" class="no-records">
            No projects assigned to this vehicle
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeModals" class="btn btn-close">Close</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      Loading vehicles data...
    </div>

    <div v-if="error" class="error">
      <div class="error-icon">⚠️</div>
      <h3>Failed to load vehicles data</h3>
      <p>Please try again later</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: 'VehiclesPage',
  data() {
    return {
      vehicles: [],
      loading: false,
      error: false,
      filters: {
        search: '',
        type: ''
      },
      // Modal states
      showCreateModal: false,
      showEditModal: false,
      showProjectsModal: false,
      selectedVehicle: null,
      creating: false,
      updating: false,
      deleting: false,
      // New vehicle data
      newVehicle: {
        vehicle: '',
        type: 'IX'
      },
      // Edit vehicle data
      editVehicleData: {
        vehicle: '',
        type: 'IX'
      }
    }
  },
  computed: {
    vehicleTypes() {
      return ['IX', 'Transit', 'Crane'];
    },
    filteredVehicles() {
      return this.vehicles.filter(vehicle => {
        const searchLower = this.filters.search.toLowerCase();
        const matchesSearch =
          vehicle.vehicle?.toLowerCase().includes(searchLower) ||
          vehicle.type?.toLowerCase().includes(searchLower);
        const matchesType = !this.filters.type || vehicle.type === this.filters.type;
        return matchesSearch && matchesType;
      });
    }
  },
  methods: {
    async getVehicles() {
      try {
        this.loading = true;
        this.error = false;
        const response = await axios.get("http://localhost:8000/vehicles");
        this.vehicles = response.data;
      } catch (error) {
        console.error("API failed to load", error);
        this.error = true;
      } finally {
        this.loading = false;
      }
    },

    async createVehicle() {
      try {
        this.creating = true;

        if (!this.newVehicle.vehicle) {
          alert('Please enter a vehicle name');
          return;
        }

        const response = await axios.post(
          "http://localhost:8000/vehicles/create",
          this.newVehicle
        );

        this.vehicles.push(response.data);
        this.closeModals();
        alert('Vehicle created successfully!');

      } catch (error) {
        console.error("Failed to create vehicle", error);
        alert('Failed to create vehicle. Please try again.');
      } finally {
        this.creating = false;
      }
    },

    async updateVehicle() {
      try {
        this.updating = true;

        if (!this.editVehicleData.vehicle) {
          alert('Please enter a vehicle name');
          return;
        }

        // Debug: Check what we're about to send
        console.log('=== UPDATE DEBUG INFO ===');
        console.log('Selected vehicle ID:', this.selectedVehicle.vehicle_id);
        console.log('Selected vehicle current type:', this.selectedVehicle.type);
        console.log('Edit form data to send:', this.editVehicleData);
        console.log('Type being sent:', this.editVehicleData.type);
        console.log('======================');

        const payload = {
          vehicle: this.editVehicleData.vehicle,
          type: this.editVehicleData.type
        };

        console.log('Final payload:', payload);

        const response = await axios.patch(
          `http://localhost:8000/vehicles/${this.selectedVehicle.vehicle_id}/update`,
          payload
        );

        console.log('API Response:', response.data);

        // Force a complete refresh of the data
        await this.getVehicles();

        this.closeModals();
        alert('Vehicle updated successfully!');

      } catch (error) {
        console.error("Failed to update vehicle", error);
        console.error("Error response:", error.response?.data);
        console.error("Error status:", error.response?.status);
        alert('Failed to update vehicle. Please try again.');
      } finally {
        this.updating = false;
      }
    },

    async deleteVehicle() {
      if (!confirm('Are you sure you want to delete this vehicle?')) {
        return;
      }

      try {
        this.deleting = true;
        await axios.post(`http://localhost:8000/vehicles/${this.selectedVehicle.vehicle_id}/remove`);
        this.vehicles = this.vehicles.filter(v => v.vehicle_id !== this.selectedVehicle.vehicle_id);
        this.closeModals();
        alert('Vehicle deleted successfully!');
      } catch (error) {
        console.error("Failed to delete vehicle", error);
        alert('Failed to delete vehicle. Please try again.');
      } finally {
        this.deleting = false;
      }
    },

    async removeVehicle(vehicle) {
      if (confirm('Are you sure you want to remove this vehicle?')) {
        try {
          await axios.post(`http://localhost:8000/vehicles/${vehicle.vehicle_id}/remove`);
          this.vehicles = this.vehicles.filter(v => v.vehicle_id !== vehicle.vehicle_id);
          alert('Vehicle removed successfully!');
        } catch (error) {
          console.error("Failed to remove vehicle", error);
          alert('Failed to remove vehicle. Please try again.');
        }
      }
    },

    getTypeClass(type) {
      const typeMap = {
        'IX': 'type-ix',
        'Transit': 'type-transit',
        'Crane': 'type-crane'
      };
      return typeMap[type] || 'type-default';
    },

    getProjectsCount(vehicle) {
      return vehicle.projects ? vehicle.projects.length : 0;
    },

    editVehicle(vehicle) {
      this.selectedVehicle = vehicle;
      this.editVehicleData = {
        vehicle: vehicle.vehicle,
        type: vehicle.type
      };
      this.showEditModal = true;
    },

    openProjectsModal(vehicle) {
      this.selectedVehicle = vehicle;
      this.showProjectsModal = true;
    },

    closeModals() {
      this.showCreateModal = false;
      this.showEditModal = false;
      this.showProjectsModal = false;
      this.selectedVehicle = null;
      this.newVehicle = {
        vehicle: '',
        type: 'IX'
      };
      this.editVehicleData = {
        vehicle: '',
        type: 'IX'
      };
    }
  },
  mounted() {
    this.getVehicles();
  }
}
</script>

<style scoped>
/* Reuse all the same base styles from Personnel Dashboard */
.vehicles-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* Controls and other existing styles remain the same */
.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.search-box {
  flex: 1;
  min-width: 300px;
}

.search-box input {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.filter-controls {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  align-items: center;
}

select {
  padding: 14px 18px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background-color: white;
  font-size: 1rem;
  transition: all 0.3s ease;
  min-width: 150px;
}

.btn-create {
  background: #10b981;
  color: white;
  padding: 14px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  font-size: 0.9rem;
}

/* Table Styles - More compact rows */
.table-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 30px;
  border: 1px solid #e2e8f0;
}

.bordered-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.8rem;
  /* Smaller font for more compact look */
  border: 1px solid #d1d5db;
}

.bordered-table th {
  background: linear-gradient(135deg, #4a5568 0%, #2d3748 100%);
  padding: 8px 6px;
  /* Reduced padding */
  text-align: left;
  font-weight: 600;
  color: white;
  border-bottom: 2px solid #d1d5db;
  border-right: 1px solid #718096;
  position: sticky;
  top: 0;
  white-space: nowrap;
}

.bordered-table td {
  padding: 6px 4px;
  /* Reduced padding for shorter rows */
  border-bottom: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  vertical-align: middle;
  /* Changed to middle for better alignment */
  height: 45px;
  /* Fixed height for consistent row height */
}

.bordered-table tr:hover {
  background-color: #f8fafc;
}

.cell-bordered {
  border-right: 1px solid #e5e7eb !important;
}

/* Updated Column Widths - More compact for vehicles */
.col-vehicle {
  width: 200px;
}

.col-type {
  width: 120px;
}

.col-projects {
  width: 120px;
}

.col-actions {
  width: 150px;
  min-width: 150px;
}

/* Type Cell Styling */
.type-cell {
  text-align: center;
  padding: 4px 2px;
  /* Reduced padding */
  border-radius: 4px;
  /* Smaller radius */
  display: flex;
  flex-direction: column;
  gap: 1px;
  /* Reduced gap */
  min-height: 35px;
  /* Smaller minimum height */
  justify-content: center;
  align-items: center;
  border: 1px solid transparent;
  font-size: 0.7rem;
  /* Smaller font */
  font-weight: 600;
}

.type-ix {
  background: #e3f2fd;
  color: #1976d2;
  border-color: #90caf9;
}

.type-transit {
  background: #e8f5e8;
  color: #2e7d32;
  border-color: #a5d6a7;
}

.type-crane {
  background: #fff3e0;
  color: #ef6c00;
  border-color: #ffcc80;
}

.type-default {
  background: #f5f5f5;
  color: #616161;
  border-color: #e0e0e0;
}

.type-default {
  background: #f5f5f5;
  color: #616161;
  border-color: #e0e0e0;
}

.type-text {
  font-size: 0.65rem;
  /* Smaller font */
  font-weight: 600;
}

/* Projects Cell Styling */
.projects-cell {
  text-align: center;
  padding: 4px 2px;
  /* Reduced padding */
  border-radius: 4px;
  /* Smaller radius */
  display: flex;
  flex-direction: column;
  gap: 1px;
  /* Reduced gap */
  min-height: 35px;
  /* Smaller minimum height */
  justify-content: center;
  align-items: center;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  cursor: pointer;
  transition: all 0.2s ease;
}

.projects-cell:hover {
  background: #e0f2fe;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.projects-count {
  font-weight: 700;
  color: #0369a1;
  font-size: 0.8rem;
  /* Smaller font */
}

.projects-text {
  font-size: 0.6rem;
  /* Smaller font */
  color: #0c4a6e;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.btn {
  padding: 5px 6px;
  /* Reduced padding */
  border: none;
  border-radius: 3px;
  /* Smaller radius */
  font-size: 0.65rem;
  /* Smaller font */
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  flex: 1;
  min-width: 40px;
  /* Smaller minimum width */
}

.btn-edit {
  background: #f59e0b;
  color: white;
}

.btn-edit:hover {
  background: #d97706;
  transform: translateY(-1px);
}

.btn-remove {
  background: #ef4444;
  color: white;
}

.btn-remove:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.btn-danger:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

/* Records Modal Styles */
.records-list {
  max-height: 300px;
  overflow-y: auto;
  margin: 20px 0;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f1f3f4;
  background: #fafafa;
  border-radius: 6px;
  margin-bottom: 8px;
}

.record-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.record-info {
  flex: 1;
  font-size: 0.9rem;
}

.record-info strong {
  color: #374151;
}

.no-records {
  text-align: center;
  padding: 20px;
  color: #6b7280;
  font-style: italic;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  /* Align close button to the right */
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

/* Modal styles remain the same */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.496);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  position: relative;
  padding: 30px;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.588);
}

.create-modal {
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.create-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 15px;
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

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: space-between;
  /* For delete button on left, other actions on right */
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.form-actions-right {
  display: flex;
  gap: 12px;
}

.btn-cancel,
.btn-close {
  background: #6b7280;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-cancel:hover,
.btn-close:hover {
  background: #4b5563;
}

.btn-submit {
  background: #667eea;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-submit:hover:not(:disabled) {
  background: #5a67d8;
  transform: translateY(-1px);
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

/* Loading, error, and empty states remain the same */
.loading {
  text-align: center;
  padding: 60px 20px;
  font-size: 1.2rem;
  color: #6c757d;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.error {
  text-align: center;
  padding: 60px 20px;
  background: #fed7d7;
  border-radius: 12px;
  margin: 20px 0;
  color: #c53030;
  border: 1px solid #feb2b2;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #a0aec0;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f7fafc;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  opacity: 0.5;
}

.excel-style-table {
  border: 1px solid #d0d7e5;
  border-collapse: separate;
  border-spacing: 0;
}

.excel-style-table th,
.excel-style-table td {
  border-right: 1px solid #d0d7e5;
  border-bottom: 1px solid #d0d7e5;
}

.excel-style-table th:first-child,
.excel-style-table td:first-child {
  border-left: 1px solid #d0d7e5;
}

.excel-style-table tr:first-child th {
  border-top: 1px solid #d0d7e5;
}

.excel-style-table tr:hover td {
  background-color: #f0f7ff;
}

@media (max-width: 1200px) {
  .table-container {
    overflow-x: auto;
  }

  .bordered-table {
    min-width: 600px;
    /* Reduced min-width for vehicles */
  }
}

@media (max-width: 768px) {
  .controls {
    flex-direction: column;
  }

  .search-box {
    min-width: 100%;
  }

  .dashboard-header {
    padding: 20px 15px;
  }

  h1 {
    font-size: 2rem;
  }

  .action-buttons {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .btn {
    flex: 1;
    min-width: 60px;
  }

  .form-actions {
    flex-direction: column;
    gap: 10px;
  }

  .form-actions-right {
    width: 100%;
    justify-content: space-between;
  }
}

/* New CSS for Edit Vehicle Form Buttons - Compact Version */
.edit-vehicle-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
  width: 100%;
}

.edit-vehicle-actions-left {
  flex: 1;
}

.edit-vehicle-actions-right {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* Compact button sizing for edit form */
.edit-vehicle-actions .btn-danger,
.edit-vehicle-actions .btn-cancel,
.edit-vehicle-actions .btn-submit {
  min-width: 120px;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Even more compact for tight spaces */
.edit-vehicle-actions.compact .btn-danger,
.edit-vehicle-actions.compact .btn-cancel,
.edit-vehicle-actions.compact .btn-submit {
  min-width: 110px;
  padding: 8px 14px;
  font-size: 0.8rem;
  height: 36px;
}

/* Specific button styles */
.edit-vehicle-actions .btn-danger {
  background: #ef4444;
  color: white;
}

.edit-vehicle-actions .btn-danger:hover:not(:disabled) {
  background: #dc2626;
  transform: translateY(-1px);
}

.edit-vehicle-actions .btn-cancel {
  background: #6b7280;
  color: white;
}

.edit-vehicle-actions .btn-cancel:hover {
  background: #4b5563;
  transform: translateY(-1px);
}

.edit-vehicle-actions .btn-submit {
  background: #667eea;
  color: white;
}

.edit-vehicle-actions .btn-submit:hover:not(:disabled) {
  background: #5a67d8;
  transform: translateY(-1px);
}

.edit-vehicle-actions .btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Responsive design for mobile */
@media (max-width: 768px) {
  .edit-vehicle-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .edit-vehicle-actions-left,
  .edit-vehicle-actions-right {
    width: 100%;
  }
  
  .edit-vehicle-actions-right {
    justify-content: space-between;
  }
  
  .edit-vehicle-actions .btn-danger,
  .edit-vehicle-actions .btn-cancel,
  .edit-vehicle-actions .btn-submit {
    min-width: 100px;
    flex: 1;
    font-size: 0.8rem;
    padding: 8px 12px;
    height: 36px;
  }
}

/* For very small screens */
@media (max-width: 480px) {
  .edit-vehicle-actions .btn-danger,
  .edit-vehicle-actions .btn-cancel,
  .edit-vehicle-actions .btn-submit {
    min-width: 90px;
    font-size: 0.75rem;
    padding: 6px 10px;
    height: 34px;
  }
}

/* Add or update these CSS rules in your <style> section */
.create-modal h3 {
  text-align: center;
  margin-bottom: 25px;
  color: #374151;
  font-size: 1.25rem;
  font-weight: 600;
}

.create-modal {
  max-width: 450px;
  max-height: 90vh;
  overflow-y: auto;
}

.edit-vehicle-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
  width: 100%;
}

.edit-vehicle-actions-left {
  flex: 1;
}

.edit-vehicle-actions-right {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* Button styles */
.edit-vehicle-actions .btn-danger,
.edit-vehicle-actions .btn-cancel,
.edit-vehicle-actions .btn-submit {
  min-width: 120px;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-vehicle-actions.compact .btn-danger,
.edit-vehicle-actions.compact .btn-cancel,
.edit-vehicle-actions.compact .btn-submit {
  min-width: 110px;
  padding: 8px 14px;
  font-size: 0.8rem;
  height: 36px;
}

.edit-vehicle-actions .btn-danger {
  background: #ef4444;
  color: white;
}

.edit-vehicle-actions .btn-danger:hover:not(:disabled) {
  background: #dc2626;
  transform: translateY(-1px);
}

.edit-vehicle-actions .btn-cancel {
  background: #6b7280;
  color: white;
}

.edit-vehicle-actions .btn-cancel:hover {
  background: #4b5563;
  transform: translateY(-1px);
}

.edit-vehicle-actions .btn-submit {
  background: #667eea;
  color: white;
}

.edit-vehicle-actions .btn-submit:hover:not(:disabled) {
  background: #5a67d8;
  transform: translateY(-1px);
}

.edit-vehicle-actions .btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Responsive design */
@media (max-width: 768px) {
  .edit-vehicle-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .edit-vehicle-actions-left,
  .edit-vehicle-actions-right {
    width: 100%;
  }
  
  .edit-vehicle-actions-right {
    justify-content: space-between;
  }
  
  .edit-vehicle-actions .btn-danger,
  .edit-vehicle-actions .btn-cancel,
  .edit-vehicle-actions .btn-submit {
    min-width: 100px;
    flex: 1;
    font-size: 0.8rem;
    padding: 8px 12px;
    height: 36px;
  }
}

@media (max-width: 480px) {
  .edit-vehicle-actions .btn-danger,
  .edit-vehicle-actions .btn-cancel,
  .edit-vehicle-actions .btn-submit {
    min-width: 90px;
    font-size: 0.75rem;
    padding: 6px 10px;
    height: 34px;
  }
}
</style>