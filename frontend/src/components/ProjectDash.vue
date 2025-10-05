<template>
  <div class="project-dashboard">
    <header class="dashboard-header">
      <h1>Project Management Dashboard</h1>
    </header>

    <div class="controls">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchTerm"
          placeholder="Search projects by name, code, location, personel, or vehicles..."
        >
      </div>
      <div class="filter-controls">
        <select v-model="selectedLocation">
          <option value="">All Locations</option>
          <option v-for="location in locations" :key="location" :value="location">
            {{ location }}
          </option>
        </select>
        <button class="btn btn-create" @click="openCreateForm">
          + Create New Project
        </button>
      </div>
    </div>

    <div class="table-container">
      <table v-if="filteredProjects.length > 0" class="bordered-table excel-style-table">
        <thead>
          <tr>
            <th class="col-code">Project Code</th>
            <th class="col-name">Project Name</th>
            <th class="col-location">Location</th>
            <th class="col-duration">Duration</th>
            <th class="col-personnel">Expected</th>
            <th class="col-crane">Crane</th>
            <th class="col-map">Coordinates</th>
            <th class="col-start">Start Date</th>
            <th class="col-end">End Date</th>
            <th class="col-personnel-list">Personnel</th>
            <th class="col-vehicles">Vehicles</th>
            <th class="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="project in filteredProjects" 
            :key="project.project_id" 
            class="table-row"
          >
            <td class="col-code cell-bordered">
              <span class="project-code clickable" @click="openProjectCard(project)">
                {{ project.project_code }}
              </span>
            </td>
            <td class="col-name cell-bordered">
              <strong>{{ project.project_name }}</strong>
            </td>
            <td class="col-location cell-bordered">
              <span class="location-badge">{{ project.location }}</span>
            </td>
            <td class="col-duration cell-bordered">
              <div class="duration-cell">
                <span class="duration-value">{{ project.duration }}</span>
                <span class="duration-unit">days</span>
              </div>
            </td>
            <td class="col-personnel cell-bordered">
              <div class="personnel-count">
                <span class="count">{{ project.expected_personel }}</span>
                <span class="label">people</span>
              </div>
            </td>
            <td class="col-crane cell-bordered">
              <span :class="['crane-indicator', project.crane === 'Yes' ? 'has-crane' : 'no-crane']">
                {{ project.crane === 'Yes' ? '✓' : '✗' }}
              </span>
            </td>
            <td class="col-map cell-bordered">
              <a 
                :href="getMapsLink(project.xy_map)" 
                target="_blank" 
                class="map-link"
                @click.stop
              >
                <span class="map-icon">🗺️</span>
                View Map
              </a>
            </td>
            <td class="col-start cell-bordered">
              <div class="date-cell">
                <span class="date">{{ formatDate(project.date_start) }}</span>
              </div>
            </td>
            <td class="col-end cell-bordered">
              <div class="date-cell">
                <span class="date">{{ calculateEndDate(project) }}</span>
              </div>
            </td>
            <td class="col-personnel-list cell-bordered">
              <div class="personel-list">
                <div 
                  v-for="person in (project.personel || [])" 
                  :key="person.personel_id"
                  class="personel-item"
                  @click.stop="openPersonelInfo(person)"
                >
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <div v-if="!project.personel || project.personel.length === 0" class="personel-item empty">
                  No personnel
                </div>
              </div>
            </td>
            <td class="col-vehicles cell-bordered">
              <div class="vehicles-list">
                <div 
                  v-for="vehicle in (project.vehicles || [])" 
                  :key="vehicle.vehicle_id"
                  class="vehicle-item"
                  @click.stop="openVehicleCard(vehicle)"
                >
                  <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                  <span class="vehicle-type">{{ vehicle.type }}</span>
                </div>
                <div v-if="!project.vehicles || project.vehicles.length === 0" class="vehicle-item empty">
                  No vehicles
                </div>
              </div>
            </td>
            <td class="col-actions cell-bordered">
              <div class="action-buttons">
                <button class="btn btn-personnel" @click.stop="openPersonnelForm(project)">
                  Personnel
                </button>
                <button class="btn btn-vehicles" @click.stop="openVehiclesForm(project)">
                  Vehicles
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty-state">
        <div class="empty-icon">📊</div>
        <h3>No projects found</h3>
        <p>Try adjusting your search or filters</p>
      </div>
    </div>

    <!-- Create Project Modal -->
    <div v-if="showCreateForm" class="modal-overlay">
      <div class="modal-content create-modal" @click.stop>
        <h3>Create New Project</h3>
        <form @submit.prevent="createProject" class="create-form">
          <div class="form-row">
            <div class="form-group">
              <label for="project_code">Project Code *</label>
              <input 
                type="text" 
                id="project_code"
                v-model="newProject.project_code" 
                required
                placeholder="Enter project code"
              >
            </div>
            <div class="form-group">
              <label for="project_name">Project Name *</label>
              <input 
                type="text" 
                id="project_name"
                v-model="newProject.project_name" 
                required
                placeholder="Enter project name"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="location">Location</label>
              <input 
                type="text" 
                id="location"
                v-model="newProject.location" 
                placeholder="Enter location"
              >
            </div>
            <div class="form-group">
              <label for="duration">Duration (days)</label>
              <input 
                type="number" 
                id="duration"
                v-model.number="newProject.duration" 
                min="1"
                placeholder="Enter duration"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="expected_personel">Expected Personnel</label>
              <input 
                type="number" 
                id="expected_personel"
                v-model.number="newProject.expected_personel"
                min="0"
                placeholder="Enter expected personnel count"
              >
            </div>
            <div class="form-group">
              <label for="crane">Crane Required</label>
              <select id="crane" v-model="newProject.crane">
                <option value="No">No</option>
                <option value="Yes">Yes</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="xy_map">Coordinates (Lat, Lng)</label>
              <input 
                type="text" 
                id="xy_map"
                v-model="newProject.xy_map" 
                placeholder="e.g., 40.7128, -74.0060"
              >
            </div>
            <div class="form-group">
              <label for="date_start">Start Date</label>
              <input 
                type="date" 
                id="date_start"
                v-model="newProject.date_start"
              >
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModals" class="btn btn-cancel">
              Cancel
            </button>
            <button type="submit" class="btn btn-submit" :disabled="creating">
              {{ creating ? 'Creating...' : 'Create Project' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Project Card Modal -->
    <div v-if="showProjectCard && selectedProject" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <button class="modal-close-btn" @click="closeModals">×</button>
        <ProjectCard :project="selectedProject" @updated="getProjects" @close="closeModals" />
      </div>
    </div>

<!-- Personel Info Modal -->
<div v-if="showPersonelInfo" class="modal-overlay personel-info-overlay">
  <div class="modal-content personel-info-modal" @click.stop>
    <div v-if="loadingPersonnel" class="personel-loading">
      <div class="spinner"></div>
      Loading personnel data...
    </div>
    <PersonelInfo 
      v-else-if="selectedPersonnel"
      :personnel="selectedPersonnel"
      @close="closePersonelInfo"
      @personel-updated="handlePersonelUpdated"
      @medical-created="handleMedicalCreated"
      @xray-created="handleXrayCreated"
      @education-created="handleEducationCreated"
      @medical-removed="handleMedicalRemoved"
      @xray-removed="handleXrayRemoved"
      @education-removed="handleEducationRemoved"
      @open-project-card="openProjectCardFromPersonel"
    />
  </div>
</div>

    <!-- Vehicle Card Modal -->
    <div v-if="showVehicleCard && selectedVehicle" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <VehiclesCard 
          :vehicle="selectedVehicle"
          :projectId="selectedProject?.project_id"
          @removed="getProjects"
          @close="closeModals"
        />
      </div>
    </div>

    <!-- Add Personnel Modal -->
    <div v-if="showPersonnelForm" class="modal-overlay" @click="closeModals">
      <div class="modal-content large-modal" @click.stop>
        <AddPersonel 
          :project="selectedProject" 
          @close="closeModals"
          @personnelUpdated="handlePersonnelUpdated"
        />
      </div>
    </div>

    <!-- Add Vehicles Modal -->
    <div v-if="showVehiclesForm" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <AddVehicles 
          :project="selectedProject" 
          @close="closeModals"
          @vehiclesUpdated="getProjects"  
        />
      </div>
    </div>

    <div v-if="showUpdateForm" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Update Project - {{ selectedProject?.project_name }}</h3>
        <p>Project update form will go here...</p>
        <button @click="closeModals" class="btn btn-close">Close</button>
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      Loading project data...
    </div>

    <div v-if="error" class="error">
      <div class="error-icon">⚠️</div>
      <h3>Failed to load project data</h3>
      <p>Please try again later</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import ProjectCard from "./ProjectCard.vue";
import PersonelInfo from "./PersonelInfo.vue";
import AddPersonel from "./AddPersonel.vue";
import VehiclesCard from "./VehiclesCard.vue";
import AddVehicles from "./AddVehicles.vue";

export default {
  name: 'ProjectDashboard',
  components: {
    ProjectCard, 
    PersonelInfo,
    AddPersonel, 
    VehiclesCard, 
    AddVehicles
  },
  data() {
    return {
      projects: [],
      loading: false,
      error: false,
      searchTerm: '',
      selectedLocation: '',
      selectedStatus: '',
      // Modal states
      showProjectCard: false,
      showPersonelInfo: false,
      showVehicleCard: false,
      showPersonnelForm: false,
      showVehiclesForm: false,
      showUpdateForm: false,
      showCreateForm: false,
      selectedProject: null,
      selectedPersonnel: null,
      selectedVehicle: null,
      creating: false,
      loadingPersonnel: false, // NEW: Add loading state for personnel data
      // New project data
      newProject: {
        project_code: '',
        project_name: '',
        location: '',
        duration: null,
        expected_personel: null,
        crane: 'No',
        xy_map: '',
        date_start: '',
        date_end: ''
      }
    }
  },
  computed: {
    locations() {
      return [...new Set(this.projects.map(p => p.location))];
    },
    filteredProjects() {
      return this.projects.filter(project => {
        // Search filter - now includes personnel and vehicles
        const searchLower = this.searchTerm.toLowerCase();
        const matchesSearch = 
          project.project_code?.toLowerCase().includes(searchLower) ||
          project.project_name?.toLowerCase().includes(searchLower) ||
          project.location?.toLowerCase().includes(searchLower) ||
          // Search in personnel
          (project.personel || []).some(person => 
            person.personel_name?.toLowerCase().includes(searchLower) ||
            person.personel_surname?.toLowerCase().includes(searchLower) ||
            person.role?.toLowerCase().includes(searchLower)
          ) ||
          // Search in vehicles
          (project.vehicles || []).some(vehicle => 
            vehicle.vehicle?.toLowerCase().includes(searchLower) ||
            vehicle.type?.toLowerCase().includes(searchLower)
          );
        
        // Location filter
        const matchesLocation = !this.selectedLocation || project.location === this.selectedLocation;
        
        // Status filter
        let matchesStatus = true;
        if (this.selectedStatus) {
          const status = this.getProjectStatus(project);
          matchesStatus = status === this.selectedStatus;
        }
        
        return matchesSearch && matchesLocation && matchesStatus;
      });
    }
  },
  methods: {
    async getProjects() {
      try {
        this.loading = true;
        this.error = false;
        const response = await axios.get("http://localhost:8000/projects");
        this.projects = response.data;
      } catch (error) {
        console.error("API failed to load", error);
        this.error = true;
      } finally {
        this.loading = false;
      }
    },

    // NEW: Fetch complete personnel data with medical, xray, and education records
    async fetchCompletePersonnelData(personelId) {
      try {
        this.loadingPersonnel = true;
        const response = await axios.get(`http://localhost:8000/personel/${personelId}`);
        return response.data;
      } catch (error) {
        console.error("Failed to load personnel data", error);
        alert('Failed to load personnel details. Please try again.');
        return null;
      } finally {
        this.loadingPersonnel = false;
      }
    },

    // UPDATED: Open PersonelInfo with complete data
    async openPersonelInfo(person) {
      try {
        // Show loading state
        this.loadingPersonnel = true;
        
        // Fetch complete personnel data including medical, xray, education
        const completePersonnelData = await this.fetchCompletePersonnelData(person.personel_id);
        
        if (completePersonnelData) {
          this.selectedPersonnel = completePersonnelData;
          this.showPersonelInfo = true;
        }
      } catch (error) {
        console.error("Error opening personnel info", error);
      } finally {
        this.loadingPersonnel = false;
      }
    },

    handlePersonnelUpdated() {
      this.getProjects();
    },

    handlePersonnelRemoved() {
      this.getProjects();
      this.closeModals();
    },

// UPDATED: Handle PersonelInfo events with data refresh
async handlePersonelUpdated() {
  // Refresh the complete personnel data
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
  // Also refresh projects to show any changes in the table
  this.getProjects();
},

// UPDATED: All record event handlers to refresh data
async handleMedicalCreated() {
  // Refresh the personnel data to get updated medical records
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
},

async handleXrayCreated() {
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
},

async handleEducationCreated() {
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
},

async handleMedicalRemoved() {
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
},

async handleXrayRemoved() {
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
},

async handleEducationRemoved() {
  if (this.selectedPersonnel) {
    const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
    if (freshData) {
      this.selectedPersonnel = freshData;
    }
  }
},

    // Close PersonelInfo modal
    closePersonelInfo() {
      this.showPersonelInfo = false;
      this.selectedPersonnel = null;
      this.loadingPersonnel = false;
    },

    // Handle project card opening from PersonelInfo
    openProjectCardFromPersonel(project) {
      this.selectedProject = project;
      this.showProjectCard = true;
      this.closePersonelInfo();
    },

    async createProject() {
      try {
        this.creating = true;
        
        if (!this.newProject.project_code || !this.newProject.project_name) {
          alert('Please fill in Project Code and Project Name');
          return;
        }

        const projectData = {
          project_code: this.newProject.project_code,
          project_name: this.newProject.project_name,
          location: this.newProject.location || null,
          duration: this.newProject.duration || null,
          expected_personel: this.newProject.expected_personel || null,
          crane: this.newProject.crane || 'No',
          xy_map: this.newProject.xy_map || null,
          date_start: this.newProject.date_start || null
        };

        const response = await axios.post("http://localhost:8000/projects/create", projectData);
        
        this.projects.push(response.data);
        this.closeModals();
        alert('Project created successfully!');
        
      } catch (error) {
        console.error("Failed to create project", error);
        alert('Failed to create project. Please try again.');
      } finally {
        this.creating = false;
      }
    },

    openCreateForm() {
      this.showCreateForm = true;
      this.newProject = {
        project_code: '',
        project_name: '',
        location: '',
        duration: null,
        expected_personel: null,
        crane: 'No',
        xy_map: '',
        date_start: ''
      };
    },

    getProjectStatus(project) {
      const today = new Date();
      const startDate = new Date(project.date_start);
      const endDate = project.date_end ? new Date(project.date_end) : null;
      
      if (startDate > today) {
        return 'upcoming';
      } else if ((!endDate || endDate >= today) && startDate <= today) {
        return 'active';
      } else {
        return 'completed';
      }
    },

    getProjectStatusText(project) {
      const status = this.getProjectStatus(project);
      const statusMap = {
        'upcoming': 'Upcoming',
        'active': 'Active',
        'completed': 'Completed'
      };
      return statusMap[status];
    },

    getMapsLink(coordinates) {
      if (!coordinates) return '#';
      const [lat, lng] = coordinates.split(', ').map(coord => parseFloat(coord));
      return `https://www.google.com/maps?q=${lat},${lng}`;
    },

    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const options = { year: 'numeric', month: 'short', day: 'numeric' };
      return new Date(dateString).toLocaleDateString('en-US', options);
    },

    // Modal methods
    openProjectCard(project) {
      this.selectedProject = project;
      this.showProjectCard = true;
    },

    openVehicleCard(vehicle) {
      this.selectedVehicle = vehicle;
      this.showVehicleCard = true;
    },

    openPersonnelForm(project) {
      this.selectedProject = project;
      this.showPersonnelForm = true;
    },

    openVehiclesForm(project) {
      this.selectedProject = project;
      this.showVehiclesForm = true;
    },

    closeModals() {
      this.showProjectCard = false;
      this.showPersonelInfo = false;
      this.showVehicleCard = false;
      this.showPersonnelForm = false;
      this.showVehiclesForm = false;
      this.showCreateForm = false;
      this.selectedProject = null;
      this.selectedPersonnel = null;
      this.selectedVehicle = null;
      this.loadingPersonnel = false;
    },

    calculateEndDate(project) {
      if (!project.date_start || !project.duration) {
        return '—';
      }
      
      const startDate = new Date(project.date_start);
      const workingDays = parseInt(project.duration);
      
      let currentDate = new Date(startDate);
      let daysAdded = 1;
      
      while (daysAdded < workingDays) {
        currentDate.setDate(currentDate.getDate() + 1);
        const dayOfWeek = currentDate.getDay();
        if (dayOfWeek !== 0 && dayOfWeek !== 6) {
          daysAdded++;
        }
      }
      
      return this.formatDate(currentDate);
    }
  },
  mounted() {
    this.getProjects();
  }
}
</script>

<style scoped>
/* Your existing CSS styles remain exactly the same, just add the PersonelInfo modal styles */
/* Add this to your CSS */
.personel-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #6c757d;
}

.personel-loading .spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}
/* Personel Info Modal Styles */
.personel-info-overlay {
  z-index: 1001; /* Higher than other modals */
}

.personel-info-modal {
  max-width: 500px;
  width: 95%;
  max-height: 90vh;
  overflow: hidden;
  padding: 0;
  background: transparent;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.personel-info-modal ::v-deep .personel-info-card {
  max-width: none;
  margin: 0;
  height: 100%;
  max-height: 90vh;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Ensure modal overlay covers everything */
.modal-overlay {
  backdrop-filter: blur(5px);
}

@media (max-width: 768px) {
  .personel-info-modal {
    width: 98%;
    max-width: 98%;
    height: 95vh;
    max-height: 95vh;
  }
  
  .personel-info-modal ::v-deep .personel-info-card {
    border-radius: 0;
    height: 100%;
  }
}

/* Rest of your existing CSS remains exactly the same */
.project-dashboard {
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


h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 700;
}

.subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  font-weight: 300;
}

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

.search-box input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
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

select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Create Project Button */
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

.btn-create:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.table-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 30px;
  border: 1px solid #e2e8f0;
}

/* Enhanced Table Borders */
.bordered-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;
  border: 1px solid #d1d5db;
}

.bordered-table th {
  background: linear-gradient(135deg, #4a5568 0%, #2d3748 100%);
  padding: 12px 8px;
  text-align: left;
  font-weight: 600;
  color: white;
  border-bottom: 2px solid #d1d5db;
  border-right: 1px solid #718096;
  position: sticky;
  top: 0;
  white-space: nowrap;
}

.bordered-table th:last-child {
  border-right: 1px solid #718096;
}

.bordered-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  vertical-align: top;
}

.bordered-table td:last-child {
  border-right: 1px solid #e5e7eb;
}

.bordered-table tr:last-child td {
  border-bottom: 1px solid #e5e7eb;
}

.bordered-table tr:hover {
  background-color: #f8fafc;
}

/* Cell borders for better separation */
.cell-bordered {
  border-right: 1px solid #e5e7eb !important;
}

.cell-bordered:last-child {
  border-right: 1px solid #e5e7eb !important;
}

/* Column Width Classes - Updated */
.col-code { width: 80px; }
.col-name { width: 120px; }
.col-location { width: 80px; }
.col-duration { width: 70px; }
.col-personnel { width: 70px; }
.col-crane { width: 50px; }
.col-map { width: 90px; }
.col-start { width: 100px; }
.col-end { width: 100px; }
.col-status { width: 80px; }
.col-personnel-list { width: 180px; min-width: 180px; }
.col-vehicles { width: 120px; min-width: 120px; }
.col-actions { width: 180px; min-width: 180px; }

/* Custom Cell Styling */
.project-code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-weight: 600;
  color: #2d3748;
  background: #f7fafc;
  padding: 4px 6px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  font-size: 0.8rem;
  display: inline-block;
}

.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
}

.clickable:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.location-badge {
  background: #edf2f7;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  color: #4a5568;
  display: inline-block;
}

.duration-cell, .personnel-count {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.duration-value, .count {
  font-size: 0.9rem;
  font-weight: 700;
  color: #2d3748;
}

.duration-unit, .label {
  font-size: 0.7rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.crane-indicator {
  display: inline-block;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
  margin: 0 auto;
}

.has-crane {
  background: #c6f6d5;
  color: #276749;
}

.no-crane {
  background: #fed7d7;
  color: #c53030;
}

.map-link {
  color: #667eea;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  font-size: 0.8rem;
  transition: color 0.2s ease;
  justify-content: center;
}

.map-link:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.map-icon {
  font-size: 0.9rem;
}

.date-cell {
  text-align: center;
}

.date {
  font-weight: 500;
  color: #2d3748;
  background: #f7fafc;
  padding: 4px 6px;
  border-radius: 4px;
  display: inline-block;
  border: 1px solid #e2e8f0;
  font-size: 0.8rem;
}

.status-badge {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  text-align: center;
  width: 100%;
}

.status-active {
  background-color: #c6f6d5;
  color: #276749;
  border: 1px solid #9ae6b4;
}

.status-upcoming {
  background-color: #bee3f8;
  color: #2c5aa0;
  border: 1px solid #90cdf4;
}

.status-completed {
  background-color: #e2e8f0;
  color: #4a5568;
  border: 1px solid #cbd5e0;
}

.personel-list, .vehicles-list {
  max-height: 120px;
  overflow-y: auto;
  padding: 2px;
}

.personel-item, .vehicle-item {
  padding: 6px 8px;
  border-bottom: 1px solid #f1f3f4;
  border-radius: 4px;
  margin-bottom: 4px;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  gap: 1px;
  border: 1px solid #f1f3f4;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.75rem;
}

.personel-item:hover, .vehicle-item:hover {
  background: #e3f2fd;
  border-color: #90caf9;
  transform: translateY(-1px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.personel-item:last-child, .vehicle-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.personel-item.empty, .vehicle-item.empty {
  background: #f7fafc;
  color: #a0aec0;
  font-style: italic;
  text-align: center;
  border: 1px dashed #e2e8f0;
  cursor: default;
}

.personel-item.empty:hover, .vehicle-item.empty:hover {
  background: #f7fafc;
  transform: none;
  box-shadow: none;
}

.person-name, .vehicle-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.75rem;
}

.person-role, .vehicle-type {
  font-size: 0.7rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

/* Action Buttons - Updated to horizontal layout */
.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.btn {
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  flex: 1;
  min-width: 50px;
}

.btn-personnel {
  background: #4caf50;
  color: white;
}

.btn-personnel:hover {
  background: #45a049;
  transform: translateY(-1px);
}

.btn-vehicles {
  background: #2196f3;
  color: white;
}

.btn-vehicles:hover {
  background: #1976d2;
  transform: translateY(-1px);
}

.btn-update {
  background: #ff9800;
  color: white;
}

.btn-update:hover {
  background: #f57c00;
  transform: translateY(-1px);
}

.btn-close {
  background: #6c757d;
  color: white;
  margin-top: 15px;
}

.btn-close:hover {
  background: #5a6268;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.496); /* slightly darker for focus */
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

.modal-close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 35px;
  cursor: pointer;
  color: #5f0101;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 1000;
}

.modal-close-btn:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.modal-content h3 {
  margin-bottom: 15px;
  color: #2d3748;
}

/* Create Project Modal Styles */
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

.form-group.full-width {
  width: 100%;
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
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.btn-cancel {
  background: #6b7280;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-cancel:hover {
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

.error h3 {
  margin-bottom: 10px;
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

.empty-state h3 {
  margin-bottom: 10px;
  color: #718096;
}

@media (max-width: 1200px) {
  .table-container {
    overflow-x: auto;
  }
  
  .bordered-table {
    min-width: 1200px;
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
  border-left: none;
  border-top: none;
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
.large-modal {
  max-width: 50%;
  width: 50%;
  max-height: 90vh;
}
</style>