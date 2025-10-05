<template>
  <div class="project-dashboard" :class="{ 'dark-mode': darkMode }">
    <header class="dashboard-header">
      <div class="header-content">
        <h1>Project Management Dashboard</h1>
        <button class="dark-mode-toggle" @click="toggleDarkMode">
          {{ darkMode ? '☀️ Light' : '🌙 Dark' }}
        </button>
      </div>
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
        <button class="btn btn-create" @click="openCreateForm">
          + Create New Project
        </button>
      </div>
    </div>

    <div class="table-container">
      <table v-if="filteredProjects.length > 0" class="bordered-table excel-style-table">
        <thead>
          <tr>
            <th class="col-code">Code</th>
            <th class="col-name">Project Name</th>
            <th class="col-location">Location</th>
            <th class="col-duration">Duration</th>
            <th class="col-expected">Expected</th>
            <th class="col-assigned">Assigned</th>
            <th class="col-personnel-breakdown">Personnel</th>
            <th class="col-vehicles-breakdown">Vehicles</th>
            <th class="col-crane">Crane</th>
            <th class="col-map">Map</th>
            <th class="col-start">Start</th>
            <th class="col-end">End</th>
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
            <td class="col-expected cell-bordered">
              <div class="personnel-count">
                <span class="count">{{ project.expected_personel || 0 }}</span>
                <span class="label">people</span>
              </div>
            </td>
            <td class="col-assigned cell-bordered">
              <div class="personnel-count">
                <span 
                  :class="[
                    'count', 
                    getPersonnelStatus(project) === 'sufficient' ? 'sufficient' : 'insufficient'
                  ]"
                >
                  {{ getAssignedPersonnelCount(project) }}
                </span>
                <span class="label">assigned</span>
              </div>
            </td>
            <td class="col-personnel-breakdown cell-bordered">
              <div class="breakdown-cell clickable" @click="openPersonnelBreakdown(project)">
                <div class="breakdown-item">
                  <span class="breakdown-count">{{ getRoleCount(project, 'Engineer') }}</span>
                  <span class="breakdown-label">Eng</span>
                </div>
                <div class="breakdown-item">
                  <span class="breakdown-count">{{ getRoleCount(project, 'Worker') }}</span>
                  <span class="breakdown-label">Wkr</span>
                </div>
                <div class="breakdown-item">
                  <span class="breakdown-count">{{ getRoleCount(project, 'Driver') }}</span>
                  <span class="breakdown-label">Drv</span>
                </div>
              </div>
            </td>
            <td class="col-vehicles-breakdown cell-bordered">
              <div class="breakdown-cell clickable" @click="openVehiclesBreakdown(project)">
                <div class="breakdown-item">
                  <span class="breakdown-count">{{ getVehicleTypeCount(project, 'Truck') }}</span>
                  <span class="breakdown-label">Truck</span>
                </div>
                <div class="breakdown-item">
                  <span class="breakdown-count">{{ getVehicleTypeCount(project, 'Car') }}</span>
                  <span class="breakdown-label">Car</span>
                </div>
                <div class="breakdown-item">
                  <span class="breakdown-count">{{ getVehicleTypeCount(project, 'Other') }}</span>
                  <span class="breakdown-label">Other</span>
                </div>
              </div>
            </td>
            <td class="col-crane cell-bordered">
              <span :class="['crane-text', project.crane === 'Yes' ? 'has-crane' : 'no-crane']">
                {{ project.crane === 'Yes' ? 'YES' : 'NO' }}
              </span>
            </td>
            <td class="col-map cell-bordered">
              <a 
                v-if="project.xy_map"
                :href="getMapsLink(project.xy_map)" 
                target="_blank" 
                class="map-link-simple"
                @click.stop
              >
                Map
              </a>
              <span v-else class="map-link-simple disabled">Map</span>
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
            <td class="col-actions cell-bordered">
              <div class="action-buttons">
                <button class="btn btn-personnel" @click.stop="openPersonnelForm(project)">
                  Personnel
                </button>
                <button class="btn btn-vehicles" @click.stop="openVehiclesForm(project)">
                  Vehicles
                </button>
                <button class="btn btn-remove" @click.stop="removeProject(project)">
                  Remove
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

    <!-- Personnel Breakdown Modal -->
    <div v-if="showPersonnelBreakdown && selectedProject" class="modal-overlay" @click="closeModals">
      <div class="modal-content breakdown-modal" @click.stop>
        <div class="breakdown-header">
          <h3>Personnel - {{ selectedProject.project_name }}</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
        <div class="breakdown-content">
          <div 
            v-for="person in (selectedProject.personel || [])" 
            :key="person.personel_id"
            class="personnel-item clickable"
            @click="openPersonelInfo(person)"
          >
            <div class="personnel-info">
              <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
              <span class="person-role">{{ person.role }}</span>
            </div>
          </div>
          <div v-if="!selectedProject.personel || selectedProject.personel.length === 0" class="no-items">
            No personnel assigned
          </div>
        </div>
      </div>
    </div>

    <!-- Vehicles Breakdown Modal -->
    <div v-if="showVehiclesBreakdown && selectedProject" class="modal-overlay" @click="closeModals">
      <div class="modal-content breakdown-modal" @click.stop>
        <div class="breakdown-header">
          <h3>Vehicles - {{ selectedProject.project_name }}</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
        <div class="breakdown-content">
          <div 
            v-for="vehicle in (selectedProject.vehicles || [])" 
            :key="vehicle.vehicle_id"
            class="vehicle-item clickable"
            @click="openVehicleCard(vehicle)"
          >
            <div class="vehicle-info">
              <span class="vehicle-name">{{ vehicle.vehicle }}</span>
              <span class="vehicle-type">{{ vehicle.type }}</span>
            </div>
          </div>
          <div v-if="!selectedProject.vehicles || selectedProject.vehicles.length === 0" class="no-items">
            No vehicles assigned
          </div>
        </div>
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
      darkMode: false,
      // Modal states
      showProjectCard: false,
      showPersonelInfo: false,
      showVehicleCard: false,
      showPersonnelForm: false,
      showVehiclesForm: false,
      showUpdateForm: false,
      showCreateForm: false,
      showPersonnelBreakdown: false,
      showVehiclesBreakdown: false,
      selectedProject: null,
      selectedPersonnel: null,
      selectedVehicle: null,
      creating: false,
      loadingPersonnel: false,
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
    filteredProjects() {
      return this.projects.filter(project => {
        const searchLower = this.searchTerm.toLowerCase();
        const matchesSearch = 
          project.project_code?.toLowerCase().includes(searchLower) ||
          project.project_name?.toLowerCase().includes(searchLower) ||
          project.location?.toLowerCase().includes(searchLower) ||
          (project.personel || []).some(person => 
            person.personel_name?.toLowerCase().includes(searchLower) ||
            person.personel_surname?.toLowerCase().includes(searchLower) ||
            person.role?.toLowerCase().includes(searchLower)
          ) ||
          (project.vehicles || []).some(vehicle => 
            vehicle.vehicle?.toLowerCase().includes(searchLower) ||
            vehicle.type?.toLowerCase().includes(searchLower)
          );
        
        return matchesSearch;
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

    // Remove project method
    async removeProject(project) {
      if (confirm(`Are you sure you want to remove project "${project.project_name}"? This action cannot be undone.`)) {
        try {
          await axios.post(`http://localhost:8000/projects/${project.project_id}/remove`);
          this.getProjects(); // Refresh the project list
          alert('Project removed successfully!');
        } catch (error) {
          console.error("Failed to remove project", error);
          alert('Failed to remove project. Please try again.');
        }
      }
    },

    // Get assigned personnel count
    getAssignedPersonnelCount(project) {
      return (project.personel || []).length;
    },

    // Check if personnel assignment is sufficient
    getPersonnelStatus(project) {
      const assigned = this.getAssignedPersonnelCount(project);
      const expected = project.expected_personel || 0;
      return assigned >= expected ? 'sufficient' : 'insufficient';
    },

    // Get count by role
    getRoleCount(project, role) {
      return (project.personel || []).filter(person => person.role === role).length;
    },

    // Get count by vehicle type
    getVehicleTypeCount(project, type) {
      return (project.vehicles || []).filter(vehicle => vehicle.type === type).length;
    },

    // Open personnel breakdown modal
    openPersonnelBreakdown(project) {
      this.selectedProject = project;
      this.showPersonnelBreakdown = true;
    },

    // Open vehicles breakdown modal
    openVehiclesBreakdown(project) {
      this.selectedProject = project;
      this.showVehiclesBreakdown = true;
    },

    // Toggle dark mode
    toggleDarkMode() {
      this.darkMode = !this.darkMode;
    },

    // Fetch complete personnel data with medical, xray, and education records
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

    // Open PersonelInfo with complete data
    async openPersonelInfo(person) {
      try {
        this.loadingPersonnel = true;
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

    // Handle PersonelInfo events with data refresh
    async handlePersonelUpdated() {
      if (this.selectedPersonnel) {
        const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
        if (freshData) {
          this.selectedPersonnel = freshData;
        }
      }
      this.getProjects();
    },

    // All record event handlers to refresh data
    async handleMedicalCreated() {
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
      this.showPersonnelBreakdown = false;
      this.showVehiclesBreakdown = false;
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
/* Dark mode variables */
:root {
  --bg-primary: #ffffff;
  --bg-secondary: #f8f9fa;
  --text-primary: #2d3748;
  --text-secondary: #4a5568;
  --border-color: #e2e8f0;
  --header-bg: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --table-header-bg: #4a3b8a;
  --shadow: rgba(0, 0, 0, 0.1);
}

.dark-mode {
  --bg-primary: #1a202c;
  --bg-secondary: #2d3748;
  --text-primary: #f7fafc;
  --text-secondary: #e2e8f0;
  --border-color: #4a5568;
  --header-bg: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  --table-header-bg: #2d3748;
  --shadow: rgba(0, 0, 0, 0.3);
}

.project-dashboard {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  min-height: 100vh;
}

.dashboard-header {
  margin-bottom: 20px;
  padding: 20px;
  background: var(--header-bg);
  color: white;
  border-radius: 12px;
  box-shadow: 0 8px 25px var(--shadow);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h1 {
  font-size: 2rem;
  margin-bottom: 0;
  font-weight: 400;
}

.dark-mode-toggle {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.dark-mode-toggle:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
  background: var(--bg-secondary);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px var(--shadow);
  border: 1px solid var(--border-color);
}

.search-box {
  flex: 1;
  min-width: 300px;
}

.search-box input {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid var(--border-color);
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.search-box input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.filter-controls {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  align-items: center;
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

.btn-create:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.table-container {
  background-color: var(--bg-primary);
  border-radius: 12px;
  box-shadow: 0 4px 20px var(--shadow);
  overflow: hidden;
  margin-bottom: 30px;
  border: 1px solid var(--border-color);
}

.bordered-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.8rem;
  border: 1px solid var(--border-color);
}

.bordered-table th {
  background: var(--table-header-bg);
  padding: 8px 6px;
  text-align: left;
  font-weight: 600;
  color: white;
  border-bottom: 2px solid var(--border-color);
  border-right: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  white-space: nowrap;
}

.bordered-table th:last-child {
  border-right: 1px solid var(--border-color);
}

.bordered-table td {
  padding: 6px 4px;
  border-bottom: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
  vertical-align: top;
  height: 35px;
}

.bordered-table td:last-child {
  border-right: 1px solid var(--border-color);
}

.bordered-table tr:last-child td {
  border-bottom: 1px solid var(--border-color);
}

.bordered-table tr:hover {
  background-color: var(--bg-secondary);
}

.cell-bordered {
  border-right: 1px solid var(--border-color) !important;
}

.cell-bordered:last-child {
  border-right: 1px solid var(--border-color) !important;
}

/* Narrower column widths */
.col-code { width: 60px; }
.col-name { width: 100px; }
.col-location { width: 70px; }
.col-duration { width: 60px; }
.col-expected { width: 60px; }
.col-assigned { width: 60px; }
.col-personnel-breakdown { width: 80px; }
.col-vehicles-breakdown { width: 80px; }
.col-crane { width: 50px; }
.col-map { width: 40px; }
.col-start { width: 80px; }
.col-end { width: 80px; }
.col-actions { width: 180px; min-width: 180px; }

.project-code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-weight: 600;
  color: var(--text-primary);
  background: var(--bg-secondary);
  padding: 3px 5px;
  border-radius: 4px;
  border: 1px solid var(--border-color);
  font-size: 0.75rem;
  display: inline-block;
}

.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
}

.clickable:hover {
  background: var(--border-color);
  transform: translateY(-1px);
}

.location-badge {
  background: var(--bg-secondary);
  padding: 3px 6px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: 500;
  color: var(--text-secondary);
  display: inline-block;
}

.duration-cell, .personnel-count {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
}

.duration-value, .count {
  font-size: 0.8rem;
  font-weight: 700;
}

.duration-unit, .label {
  font-size: 0.65rem;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.count.sufficient {
  color: #276749;
}

.count.insufficient {
  color: #c53030;
}

/* Breakdown cells */
.breakdown-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 2px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1px 3px;
  border-radius: 3px;
  background: var(--bg-secondary);
}

.breakdown-count {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--text-primary);
}

.breakdown-label {
  font-size: 0.6rem;
  color: var(--text-secondary);
  text-transform: uppercase;
}

/* Crane styling */
.crane-text {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  padding: 3px 6px;
  border-radius: 4px;
  display: inline-block;
  text-align: center;
  width: 100%;
}

.crane-text.has-crane {
  background: #c6f6d5;
  color: #276749;
}

.crane-text.no-crane {
  background: #fed7d7;
  color: #c53030;
}

/* Map link */
.map-link-simple {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.75rem;
  transition: color 0.2s ease;
  text-align: center;
  display: block;
}

.map-link-simple:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.map-link-simple.disabled {
  color: var(--text-secondary);
  cursor: not-allowed;
  text-decoration: none;
}

.date-cell {
  text-align: center;
}

.date {
  font-weight: 500;
  color: var(--text-primary);
  background: var(--bg-secondary);
  padding: 3px 5px;
  border-radius: 4px;
  border: 1px solid var(--border-color);
  font-size: 0.75rem;
  display: inline-block;
}

.action-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.btn {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  flex: 1;
  min-width: 45px;
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

.btn-remove {
  background: #dc2626;
  color: white;
}

.btn-remove:hover {
  background: #b91c1c;
  transform: translateY(-1px);
}

/* Breakdown modals */
.breakdown-modal {
  max-width: 400px;
  max-height: 60vh;
  padding: 0;
}

.breakdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
}

.breakdown-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.breakdown-content {
  padding: 15px;
  max-height: 50vh;
  overflow-y: auto;
}

.personnel-item, .vehicle-item {
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  margin-bottom: 8px;
  background: var(--bg-secondary);
  transition: all 0.2s ease;
}

.personnel-item:hover, .vehicle-item:hover {
  background: var(--border-color);
  transform: translateY(-1px);
}

.personnel-info, .vehicle-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.person-name, .vehicle-name {
  font-weight: 600;
  font-size: 0.9rem;
}

.person-role, .vehicle-type {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.no-items {
  text-align: center;
  color: var(--text-secondary);
  font-style: italic;
  padding: 20px;
}

/* Rest of the existing styles remain similar but with dark mode variables */
.modal-overlay {
  background: rgba(0, 0, 0, 0.6);
}

.modal-content {
  background: var(--bg-primary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.loading, .error, .empty-state {
  background: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

/* Responsive design */
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
    padding: 15px;
  }
  
  h1 {
    font-size: 1.6rem;
  }
  
  .header-content {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .action-buttons {
    flex-direction: row;
    flex-wrap: wrap;
  }
}
</style>