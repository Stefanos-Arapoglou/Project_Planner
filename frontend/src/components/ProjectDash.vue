<template>
  <div class="project-dashboard">
    <div class="dashboard-header">
        <h1>Project Management Dashboard</h1>
    </div>

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
            <td class="col-assigned cell-bordered" :class="getPersonnelStatusClass(project)">
              <div class="personnel-count">
                <span class="count">
                  {{ getAssignedPersonnelCount(project) }}
                </span>
                <span class="label">assigned</span>
              </div>
            </td>
            <td class="col-personnel-breakdown cell-bordered">
              <div class="breakdown-cell clickable" @click="openPersonnelBreakdown(project)">
                <div class="breakdown-row">
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
              </div>
            </td>
            <td class="col-vehicles-breakdown cell-bordered">
              <div class="breakdown-cell clickable" @click="openVehiclesBreakdown(project)">
                <div class="breakdown-row">
                  <div class="breakdown-item">
                    <span class="breakdown-count">{{ getVehicleTypeCount(project, 'IX') }}</span>
                    <span class="breakdown-label">IX</span>
                  </div>
                  <div class="breakdown-item">
                    <span class="breakdown-count">{{ getVehicleTypeCount(project, 'Crane') }}</span>
                    <span class="breakdown-label">Crn</span>
                  </div>
                  <div class="breakdown-item">
                    <span class="breakdown-count">{{ getVehicleTypeCount(project, 'Transit') }}</span>
                    <span class="breakdown-label">Trs</span>
                  </div>
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

    <!-- CREATE PROJECT MODAL - FIXED POSITION -->
    <div v-if="showCreateForm" class="modal-overlay">
      <div class="modal-content create-modal" @click.stop>
        <div class="modal-header">
          <h3>Create New Project</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
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
    <div v-if="showPersonnelBreakdown && selectedProject" class="modal-overlay" @click="closePersonnelBreakdown">
      <div class="modal-content breakdown-modal" @click.stop>
        <div class="modal-header">
          <h3>Personnel - {{ selectedProject.project_name }}</h3>
          <button class="modal-close-btn" @click="closePersonnelBreakdown">×</button>
        </div>
        <div class="breakdown-content">
          <div 
            v-for="person in (selectedProject.personel || [])" 
            :key="person.personel_id"
            class="personnel-item clickable"
            @click="openPersonelInfoFromBreakdown(person)"
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
        <div class="modal-header">
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
        <div class="modal-header">
          <h3>Vehicle Details</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
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

    // Check if personnel assignment is sufficient and return class
    getPersonnelStatusClass(project) {
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

    // Close personnel breakdown modal only
    closePersonnelBreakdown() {
      this.showPersonnelBreakdown = false;
    },

    // Open vehicles breakdown modal
    openVehiclesBreakdown(project) {
      this.selectedProject = project;
      this.showVehiclesBreakdown = true;
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

    // Open PersonelInfo from breakdown
    async openPersonelInfoFromBreakdown(person) {
      try {
        this.loadingPersonnel = true;
        const completePersonnelData = await this.fetchCompletePersonnelData(person.personel_id);
        
        if (completePersonnelData) {
          this.selectedPersonnel = completePersonnelData;
          this.showPersonnelBreakdown = false; // Close breakdown modal
          this.showPersonelInfo = true; // Open personnel info modal
        }
      } catch (error) {
        console.error("Error opening personnel info", error);
      } finally {
        this.loadingPersonnel = false;
      }
    },

    // Open PersonelInfo directly
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

    // Close PersonelInfo modal only
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

/* .header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h1 {
  font-size: 2rem;
  margin-bottom: 0;
  font-weight: 400;
} */

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
  border: 1px solid #e2e8f0;
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
  position: relative;
}

/* FIXED: Proper table grid styling with sticky header */
.bordered-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.8rem;
  border: 1px solid #d1d5db;
}

.bordered-table thead {
  position: sticky;
  top: 0;
  z-index: 10;
}

.bordered-table th {
  background: linear-gradient(135deg, #4a5568 0%, #2d3748 100%);
  padding: 8px 6px; /* Reduced padding */
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
  border-right: 1px solid #5a4a9a;
}

.bordered-table td {
  padding: 6px 4px;
  border-bottom: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  vertical-align: top;
  height: 32px; /* Even shorter rows */
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

.cell-bordered {
  border-right: 1px solid #e5e7eb !important;
}

.cell-bordered:last-child {
  border-right: 1px solid #e5e7eb !important;
}

/* Assigned column status colors */
.col-assigned.sufficient {
  background-color: #f0fff4 !important; /* Soft green */
}

.col-assigned.insufficient {
  background-color: #fff5f5 !important; /* Soft red */
}

.bordered-table tr:hover .col-assigned.sufficient {
  background-color: #e6ffed !important;
}

.bordered-table tr:hover .col-assigned.insufficient {
  background-color: #ffe6e6 !important;
}

/* Even narrower column widths */
.col-code { width: 55px; }
.col-name { width: 90px; }
.col-location { width: 65px; }
.col-duration { width: 55px; }
.col-expected { width: 55px; }
.col-assigned { width: 55px; }
.col-personnel-breakdown { width: 70px; } /* Shorter */
.col-vehicles-breakdown { width: 70px; } /* Shorter */
.col-crane { width: 45px; }
.col-map { width: 35px; }
.col-start { width: 75px; }
.col-end { width: 75px; }
.col-actions { width: 170px; min-width: 170px; }

.project-code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-weight: 600;
  color: #2d3748;
  background: #f7fafc;
  padding: 2px 4px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  font-size: 0.7rem;
  display: inline-block;
}

.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
}

.clickable:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.location-badge {
  background: #edf2f7;
  padding: 2px 5px;
  border-radius: 10px;
  font-size: 0.65rem;
  font-weight: 500;
  color: #4a5568;
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
  font-size: 0.75rem;
  font-weight: 700;
}

.duration-unit, .label {
  font-size: 0.6rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.count.sufficient {
  color: #276749;
}

.count.insufficient {
  color: #c53030;
}

/* FIXED: Even more compact breakdown cells */
.breakdown-cell {
  display: flex;
  flex-direction: column;
  gap: 1px;
  padding: 1px;
}

.breakdown-row {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0px 2px;
  border-radius: 2px;
  background: #f7fafc;
  font-size: 0.6rem;
  min-height: 14px;
}

.breakdown-count {
  font-size: 0.65rem;
  font-weight: 700;
  color: #2d3748;
}

.breakdown-label {
  font-size: 0.55rem;
  color: #718096;
  text-transform: uppercase;
}

/* FIXED: Crane text to fit in column */
.crane-text {
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  padding: 2px 4px;
  border-radius: 3px;
  display: inline-block;
  text-align: center;
  width: 100%;
  box-sizing: border-box;
}

.crane-text.has-crane {
  background: #c6f6d5;
  color: #276749;
}

.crane-text.no-crane {
  background: #fed7d7;
  color: #c53030;
}

.map-link-simple {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.7rem;
  transition: color 0.2s ease;
  text-align: center;
  display: block;
}

.map-link-simple:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.map-link-simple.disabled {
  color: #a0aec0;
  cursor: not-allowed;
  text-decoration: none;
}

.date-cell {
  text-align: center;
}

.date {
  font-weight: 500;
  color: #2d3748;
  background: #f7fafc;
  padding: 2px 4px;
  border-radius: 3px;
  border: 1px solid #e2e8f0;
  font-size: 0.7rem;
  display: inline-block;
}

.action-buttons {
  display: flex;
  gap: 3px;
  justify-content: center;
}

.btn {
  padding: 3px 6px;
  border: none;
  border-radius: 3px;
  font-size: 0.65rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  flex: 1;
  min-width: 40px;
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

/* FIXED: Modal positioning - all modals should be fixed overlay */
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
backdrop-filter: blur(5px);
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

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8f9fa;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.3rem;
  color: #2d3748;
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
  background-color: #e5e7eb;
  color: #374151;
}

.create-modal {
  max-width: 600px;
}

.create-form {
  padding: 20px;
}

/* Breakdown modals */
.breakdown-modal {
  max-width: 350px;
  max-height: 60vh;
}

.breakdown-content {
  padding: 15px;
  max-height: 50vh;
  overflow-y: auto;
}

.personnel-item, .vehicle-item {
  padding: 8px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  margin-bottom: 6px;
  background: #f8f9fa;
  transition: all 0.2s ease;
}

.personnel-item:hover, .vehicle-item:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.personnel-info, .vehicle-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.person-name, .vehicle-name {
  font-weight: 600;
  font-size: 0.85rem;
}

.person-role, .vehicle-type {
  font-size: 0.75rem;
  color: #718096;
}

.no-items {
  text-align: center;
  color: #718096;
  font-style: italic;
  padding: 20px;
}

.large-modal {
  max-width: 50%;
  width: 50%;
  max-height: 90vh;
}

/* Rest of existing styles... */

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