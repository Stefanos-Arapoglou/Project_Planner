<template>
  <div class="calendar-view">
    <div class="calendar-header">
      <h2 class="title">Vehicles Calendar</h2>
      <div class="header-controls">
        <div class="control-group">
          <label class="control-label">Number of Days:</label>
          <div class="input-with-button">
            <input 
              type="number" 
              v-model.number="calendarDays" 
              min="1" 
              max="365"
              class="days-input"
            />
            <button class="refresh-btn" @click="generateDates">Refresh</button>
          </div>
        </div>
        <div class="control-group">
          <label class="control-label">Start Date:</label>
          <input 
            type="date" 
            v-model="startDate" 
            class="date-input"
            @change="generateDates"
          />
        </div>
        <button class="today-btn" @click="goToToday">Today</button>
      </div>
    </div>

    <div class="table-container">
      <table class="calendar-table">
        <!-- Month Header Row -->
        <thead class="month-header">
          <tr>
            <th class="sticky-col month-col" colspan="3">Timeline</th>
            <th 
              v-for="month in monthHeaders" 
              :key="month.key"
              :colspan="month.days"
              class="month-header-cell"
            >
              {{ month.name }} {{ month.year }}
            </th>
          </tr>
        </thead>
        
        <!-- Date Header Row -->
        <thead class="date-header">
          <tr>
            <th class="sticky-col col-vehicle fixed-column" 
                style="width: 200px; min-width: 200px; max-width: 200px;"
                @click="sortBy('vehicle')">
              <div class="header-content">
                Vehicle
                <span class="sort-indicator">{{ sortField === 'vehicle' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
            <th class="sticky-col col-type fixed-column" 
                style="width: 120px; min-width: 120px; max-width: 120px;"
                @click="sortBy('type')">
              <div class="header-content">
                Type
                <span class="sort-indicator">{{ sortField === 'type' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
            <!-- <th class="sticky-col col-status fixed-column" 
                style="width: 100px; min-width: 100px; max-width: 100px;"
                @click="sortBy('status')">
              <div class="header-content">
                Status
                <span class="sort-indicator">{{ sortField === 'status' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th> -->
            <th 
              v-for="date in dateRange" 
              :key="date.getTime()"
              :class="{
                'date-header-cell': true,
                'today': isToday(date),
                'weekend': isWeekend(date),
                'month-start': isMonthStart(date)
              }"
            >
              <div class="date-content">
                <div class="date-day">{{ formatDateDay(date) }}</div>
                <div class="date-weekday">{{ formatDateWeekday(date) }}</div>
              </div>
            </th>
          </tr>
        </thead>
        
        <tbody>
          <tr 
            v-for="vehicle in sortedVehicles" 
            :key="vehicle.vehicle_id"
            class="vehicle-row"
          >
            <td class="sticky-col col-vehicle fixed-column" 
                style="width: 200px; min-width: 200px; max-width: 200px;">
              <div class="vehicle-info">
                <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                <span class="vehicle-id">ID: {{ vehicle.vehicle_id }}</span>
              </div>
            </td>
            <td class="sticky-col col-type fixed-column" 
                style="width: 120px; min-width: 120px; max-width: 120px;">
              <div :class="['type-cell', getTypeClass(vehicle.type)]">
                <span class="type-text">{{ vehicle.type }}</span>
              </div>
            </td>
            <!-- <td class="sticky-col col-status fixed-column" 
                style="width: 100px; min-width: 100px; max-width: 100px;">
              <div :class="['status-cell', `status-${getVehicleStatus(vehicle)}`]">
                <span class="status-text">{{ getVehicleStatus(vehicle) }}</span>
              </div>
            </td> -->
            <td 
              v-for="date in dateRange" 
              :key="date.getTime()"
              :class="{
                'calendar-cell': true,
                'weekend': isWeekend(date),
                'today': isToday(date),
                'clickable': true
              }"
              @click="onDateClick(vehicle, date)"
            >
              <div class="cell-content">
                <div 
                  v-if="getProjectsOnDate(vehicle, date).length > 0"
                  class="projects-container"
                  :class="getProjectCountClass(getProjectsOnDate(vehicle, date).length)"
                >
                  <div 
                    v-for="project in getProjectsOnDate(vehicle, date)" 
                    :key="project.project_id"
                    class="project-chip"
                    @click.stop="onProjectClick(project)"
                  >
                    {{ project.project_code }}
                  </div>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Project Info Overlay Popup -->
    <div v-if="showProjectInfo" class="project-info-overlay" @click="handleProjectInfoClose">
      <div class="project-info-popup-container" @click.stop>
        <ProjectInfo 
          :project="selectedProject"
          :personnel="selectedProjectPersonnel"
          :vehicles="selectedProjectVehicles"
          @close="handleProjectInfoClose"
          @project-updated="handleProjectUpdated"
          @edit-personnel="handleEditPersonnel"
          @edit-vehicles="handleEditVehicles"
          @open-personnel-card="handleOpenPersonnelCard"
          @open-vehicle-card="handleOpenVehicleCard"
          @change-start-date="handleChangeStartDate"
        />
      </div>
    </div>

    <!-- Add Personnel Overlay -->
    <div v-if="showAddPersonnel" class="project-info-overlay" @click="showAddPersonnel = false">
      <div class="large-popup-container" @click.stop>
        <AddPersonel 
          :project="selectedProjectForEdit"
          @close="showAddPersonnel = false"
          @personnelUpdated="handleProjectUpdated"
        />
      </div>
    </div>

    <!-- Add Vehicles Overlay -->
    <div v-if="showAddVehicles" class="project-info-overlay" @click="showAddVehicles = false">
      <div class="large-popup-container" @click.stop>
        <AddVehicles 
          :project="selectedProjectForEdit"
          @close="showAddVehicles = false"
          @vehiclesUpdated="handleProjectUpdated"
        />
      </div>
    </div>

    <!-- Vehicle Details Modal -->
    <div v-if="showVehicleModal" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Vehicle Details</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedVehicle" class="vehicle-details">
            <div class="detail-row">
              <strong>Vehicle ID:</strong>
              <span>{{ selectedVehicle.vehicle_id }}</span>
            </div>
            <div class="detail-row">
              <strong>Vehicle Name:</strong>
              <span>{{ selectedVehicle.vehicle }}</span>
            </div>
            <div class="detail-row">
              <strong>Type:</strong>
              <span>{{ selectedVehicle.type }}</span>
            </div>
            <div class="detail-row">
              <strong>Status:</strong>
              <span :class="['vehicle-status', `status-${getVehicleStatus(selectedVehicle)}`]">
                {{ getVehicleStatus(selectedVehicle) }}
              </span>
            </div>
            <div class="detail-row">
              <strong>Assigned Projects:</strong>
              <span>{{ selectedVehicle.projects ? selectedVehicle.projects.length : 0 }} total</span>
            </div>
          </div>
          <div class="modal-actions">
            <button class="btn btn-close" @click="closeModals">Close</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">Loading vehicles...</div>
    </div>

    <!-- Error State -->
    <div v-if="error" class="error-message">
      <div class="error-content">
        <h3>Error Loading Vehicles</h3>
        <p>{{ error }}</p>
        <button class="btn btn-retry" @click="getVehicles">Retry</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import ProjectInfo from "./ProjectInfo.vue";
import AddPersonel from "./AddPersonel.vue";
import AddVehicles from "./AddVehicles.vue";

export default {
  name: "VehiclesCalendar",
  components: {
    ProjectInfo,
    AddPersonel,
    AddVehicles
  },
  data() {
    return {
      vehicles: [],
      projects: [],
      calendarDays: 30,
      startDate: new Date().toISOString().split('T')[0],
      dateRange: [],
      sortField: 'vehicle',
      sortDirection: 'asc',
      loading: false,
      error: null,
      
      // Project Info states
      showProjectInfo: false,
      selectedProject: null,
      selectedProjectPersonnel: [],
      selectedProjectVehicles: [],
      
      // Add Personnel/Vehicles states
      showAddPersonnel: false,
      showAddVehicles: false,
      selectedProjectForEdit: null,
      
      // Vehicle modal state
      showVehicleModal: false,
      selectedVehicle: null
    };
  },
  computed: {
    sortedVehicles() {
      return [...this.vehicles].sort((a, b) => {
        let aValue, bValue;
        
        if (this.sortField === 'type') {
          aValue = a.type || '';
          bValue = b.type || '';
        } else if (this.sortField === 'status') {
          aValue = this.getVehicleStatus(a);
          bValue = this.getVehicleStatus(b);
        } else {
          // Sort by vehicle name (default)
          aValue = (a.vehicle || '').toLowerCase();
          bValue = (b.vehicle || '').toLowerCase();
        }

        if (this.sortDirection === 'asc') {
          return aValue < bValue ? -1 : aValue > bValue ? 1 : 0;
        } else {
          return aValue > bValue ? -1 : aValue < bValue ? 1 : 0;
        }
      });
    },
    monthHeaders() {
      if (this.dateRange.length === 0) return [];
      
      const months = [];
      let currentMonth = null;
      
      this.dateRange.forEach(date => {
        const monthKey = `${date.getFullYear()}-${date.getMonth()}`;
        const monthName = date.toLocaleDateString("en-US", { month: "long" });
        const year = date.getFullYear();
        
        if (!currentMonth || currentMonth.key !== monthKey) {
          if (currentMonth) {
            months.push(currentMonth);
          }
          currentMonth = {
            key: monthKey,
            name: monthName,
            year: year,
            days: 1
          };
        } else {
          currentMonth.days++;
        }
      });
      
      if (currentMonth) {
        months.push(currentMonth);
      }
      
      return months;
    }
  },
  methods: {
    async getVehicles() {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.get("http://localhost:8000/vehicles");
        this.vehicles = response.data;
        
        await this.loadProjectsForAllVehicles();
        
        this.$forceUpdate();
        
      } catch (error) {
        console.error('Error fetching vehicles:', error);
        this.error = error.message || 'Failed to load vehicles';
      } finally {
        this.loading = false;
      }
    },

    async getProjects() {
      try {
        await axios.get("http://localhost:8000/projects");
      } catch (error) {
        console.error('Error fetching projects:', error);
      }
    },

    async loadProjectsForAllVehicles() {
      try {
        const promises = this.vehicles.map(async (vehicle) => {
          try {
            const response = await axios.get(`http://localhost:8000/vehicles/${vehicle.vehicle_id}/projects`);
            vehicle.projects = response.data; 
          } catch (error) {
            console.error(`Failed to load projects for vehicle ${vehicle.vehicle_id}`, error);
            vehicle.projects = [];
          }
        });
        
        await Promise.all(promises);
        
      } catch (error) {
        console.error("Failed to load projects data", error);
      }
    },

    getProjectsOnDate(vehicle, date) {
      if (!vehicle.projects || vehicle.projects.length === 0) {
        return [];
      }
      
      const projectsOnDate = vehicle.projects.filter(project => {
        if (!project.date_start) {
          return false;
        }
        
        const startDate = new Date(project.date_start);
        const endDate = this.calculateProjectEndDate(project);
        
        // Normalize dates for comparison (ignoring time)
        const checkDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        const normStart = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
        const normEnd = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
        
        const isInRange = checkDate >= normStart && checkDate <= normEnd;
        const isWeekend = this.isWeekend(date);
        
        // Filter out projects that fall on weekends (assuming projects don't run on weekends)
        return isInRange && !isWeekend;
      });
      
      return projectsOnDate;
    },

    calculateProjectEndDate(project) {
      const startDate = new Date(project.date_start);
      const workingDays = project.duration || 0; 
      let currentDate = new Date(startDate);
      
      if (workingDays <= 1) {
        return new Date(startDate);
      }
      
      let additionalDaysNeeded = workingDays - 1;
      let daysAdded = 0;
      
      // Calculate end date by skipping weekends
      while (daysAdded < additionalDaysNeeded) {
        currentDate.setDate(currentDate.getDate() + 1);
        // 0 = Sunday, 6 = Saturday
        if (currentDate.getDay() !== 0 && currentDate.getDay() !== 6) {
          daysAdded++;
        }
      }
      
      return new Date(currentDate);
    },

    getProjectCountClass(count) {
      if (count === 0) return '';
      if (count <= 1) return 'congestion-low'; 
      if (count <= 3) return 'congestion-medium';
      return 'congestion-high';
    },

    generateDates() {
      const start = new Date(this.startDate);
      this.dateRange = [];
      for (let i = 0; i < this.calendarDays; i++) {
        const d = new Date(start);
        d.setDate(start.getDate() + i);
        this.dateRange.push(d);
      }
    },

    formatDateDay(date) {
      if (!(date instanceof Date) || isNaN(date)) return '';
      return date.getDate();
    },

    formatDateWeekday(date) {
      if (!(date instanceof Date) || isNaN(date)) return '';
      return date.toLocaleDateString("en-US", { weekday: "short" });
    },

    formatFullDate(date) {
      if (!(date instanceof Date) || isNaN(date)) return '';
      return date.toLocaleDateString("en-US", { 
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },

    isToday(date) {
      if (!(date instanceof Date) || isNaN(date)) return false;
      const today = new Date();
      return date.toDateString() === today.toDateString();
    },

    isWeekend(date) {
      if (!(date instanceof Date) || isNaN(date)) return false;
      return date.getDay() === 0 || date.getDay() === 6;
    },

    isMonthStart(date) {
      if (!(date instanceof Date) || isNaN(date)) return false;
      return date.getDate() === 1;
    },

    goToToday() {
      this.startDate = new Date().toISOString().split('T')[0];
      this.generateDates();
    },

    sortBy(field) {
      if (this.sortField === field) {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortField = field;
        this.sortDirection = 'asc';
      }
    },

    onDateClick(vehicle, date) {
      const projectsOnDate = this.getProjectsOnDate(vehicle, date);
      if (projectsOnDate.length > 0) {
        // If there are projects, do nothing here, as the project chips handle clicks.
      } 
      
      // If the cell is clicked (and the click wasn't stopped by a project chip), show vehicle details
      this.selectedVehicle = vehicle;
      this.showVehicleModal = true;
    },

    async onProjectClick(project) {
      try {
        this.selectedProject = project;
        
        // Fetch assigned personnel for this project
        const personnelResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}/personel`);
        this.selectedProjectPersonnel = personnelResponse.data;
        
        // Fetch assigned vehicles for this project  
        const vehiclesResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}/vehicles`);
        this.selectedProjectVehicles = vehiclesResponse.data;
        
        this.showProjectInfo = true;
        document.body.style.overflow = 'hidden';
      } catch (error) {
        console.error('Error fetching project details:', error);
        // Fallback: show basic info even if additional data fails
        this.selectedProjectPersonnel = [];
        this.selectedProjectVehicles = [];
        this.showProjectInfo = true;
        document.body.style.overflow = 'hidden';
      }
    },

    handleProjectInfoClose() {
      this.showProjectInfo = false;
      this.selectedProject = null;
      this.selectedProjectPersonnel = [];
      this.selectedProjectVehicles = [];
      document.body.style.overflow = '';
    },

    handleProjectUpdated() {
      // Refresh all data when project is updated
      this.getVehicles();
      this.getProjects();
      this.handleProjectInfoClose();
      this.showAddPersonnel = false;
      this.showAddVehicles = false;
    },

    handleEditPersonnel(project) {
      this.selectedProjectForEdit = project;
      this.showAddPersonnel = true;
      document.body.style.overflow = 'hidden';
    },

    handleEditVehicles(project) {
      this.selectedProjectForEdit = project;
      this.showAddVehicles = true;
      document.body.style.overflow = 'hidden';
    },

    handleOpenPersonnelCard(person, project) {
      console.log('Open personnel card:', person, 'for project:', project);
      // Implement personnel card opening logic
    },

    handleOpenVehicleCard(vehicle, project) {
      console.log('Open vehicle card:', vehicle, 'for project:', project);
      // Implement vehicle card opening logic
    },

    handleChangeStartDate(newStartDate) {
      console.log('Change project start date to:', newStartDate);
      // Implement start date change logic
      if (this.selectedProject) {
        this.updateProjectStartDate(this.selectedProject.project_id, newStartDate);
      }
    },

    async updateProjectStartDate(projectId, newStartDate) {
      try {
        await axios.put(`http://localhost:8000/projects/${projectId}`, {
          date_start: newStartDate
        });
        this.handleProjectUpdated(); // Refresh data
      } catch (error) {
        console.error('Error updating project start date:', error);
      }
    },

    getProjectStatus(project) {
      const today = new Date();
      const startDate = new Date(project.date_start);
      const endDate = this.calculateProjectEndDate(project);
      
      if (today < startDate) return 'upcoming';
      if (today > endDate) return 'completed';
      return 'active';
    },

    // Vehicle type styling based on your database types
    getTypeClass(type) {
      const typeMap = {
        'IX': 'type-ix',
        'Transit': 'type-transit',
        'Crane': 'type-crane'
      };
      return typeMap[type] || 'type-default';
    },

    // Simplified vehicle status - based on project assignments
    getVehicleStatus(vehicle) {
      if (!vehicle.projects || vehicle.projects.length === 0) {
        return 'AVAILABLE';
      }

      const today = new Date();
      const hasActiveProject = vehicle.projects.some(project => {
        const startDate = new Date(project.date_start);
        const endDate = this.calculateProjectEndDate(project);
        return today >= startDate && today <= endDate;
      });

      return hasActiveProject ? 'IN_USE' : 'AVAILABLE';
    },

    closeModals() {
      this.showVehicleModal = false;
      this.showProjectInfo = false;
      this.showAddPersonnel = false;
      this.showAddVehicles = false;
      this.selectedProject = null;
      this.selectedVehicle = null;
      this.selectedProjectPersonnel = [];
      this.selectedProjectVehicles = [];
      document.body.style.overflow = '';
    }
  },
  mounted() {
    this.getVehicles();
    this.getProjects();
    this.generateDates();
  }
};
</script>

<style scoped>
/* Overlay Popup Styles */
.project-info-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 20px;
  box-sizing: border-box;
}

.project-info-popup-container {
  max-width: 90%;
  max-height: 90vh;
  overflow: auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* Ensure the ProjectInfo component fills the container */
.project-info-popup-container ::v-deep .project-info-card {
  max-width: 100%;
  margin: 0;
  border: none;
  border-radius: 12px;
  box-shadow: none;
}

.large-popup-container {
  max-height: 90vh;
  overflow: auto;
  width: auto;
  max-width: 850px;
  min-width: 650px;
}

/* Fixed width columns */
.fixed-column {
  position: sticky;
  left: 0;
  background: white;
  z-index: 2;
}

/* Rest of your existing CSS remains exactly the same */
.calendar-view {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #f8fafc;
  min-height: 100vh;
  padding: 20px;
}

.calendar-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #e2e8f0;
}

.title {
  font-size: 1.5rem;
  margin: 0;
  color: #1a365d;
  font-weight: 600;
}

.header-controls {
  display: flex;
  gap: 20px;
  align-items: center;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.control-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #4a5568;
}

.input-with-button {
  display: flex;
  gap: 8px;
  align-items: center;
}

.days-input, .date-input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
  width: 120px;
}

.days-input:focus, .date-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.refresh-btn, .today-btn {
  padding: 8px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.refresh-btn:hover, .today-btn:hover {
  background: #2563eb;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  overflow-x: auto;
}

.calendar-table {
  width: 100%;
  border-collapse: collapse;
  min-width: max-content;
}

.month-header {
  position: sticky;
  top: 0;
  z-index: 40;
  background: #1f2937;
}

.month-header-cell {
  background: #374151;
  color: white;
  font-weight: 600;
  padding: 12px 8px;
  text-align: center;
  border-left: 1px solid #4b5563;
  font-size: 0.9rem;
}

.month-col {
  background: #1f2937;
  color: white;
  font-weight: 600;
  padding: 12px;
  text-align: center;
  font-size: 0.9rem;
}

.date-header {
  position: sticky;
  top: 53px;
  z-index: 35;
  background: #f9fafb;
}

.date-header-cell {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-bottom: 2px solid #e5e7eb;
  padding: 8px 4px;
  text-align: center;
  font-size: 0.8rem;
  min-width: 50px;
  max-width: 50px;
  height: 60px;
  vertical-align: top;
}

.date-header-cell.today {
  background: #dbeafe;
  color: #1e40af;
}

.date-header-cell.weekend {
  background: #fef2f2;
  color: #dc2626;
}

.date-header-cell.month-start {
  border-left: 2px solid #9ca3af;
}

.date-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.date-day {
  font-size: 1rem;
  font-weight: 600;
}

.date-weekday {
  font-size: 0.7rem;
  opacity: 0.8;
  text-transform: uppercase;
}

.sticky-col {
  position: sticky;
  left: 0;
  background: #f9fafb;
  z-index: 30;
  border-right: 2px solid #e5e7eb;
  padding: 8px 12px;
}

.col-vehicle {
  min-width: 200px;
  left: 0;
  z-index: 31;
}

.col-type {
  min-width: 120px;
  left: 200px;
  z-index: 30;
}

.col-status {
  min-width: 100px;
  left: 320px;
  z-index: 30;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.sort-indicator {
  font-size: 0.8rem;
}

.vehicle-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.vehicle-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 0.9rem;
}

.vehicle-id {
  font-size: 0.8rem;
  color: #6b7280;
}

.type-cell {
  text-align: center;
  padding: 6px 8px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
}

.type-ix {
  background: #e3f2fd;
  color: #1976d2;
  border: 1px solid #90caf9;
}

.type-transit {
  background: #e8f5e8;
  color: #2e7d32;
  border: 1px solid #a5d6a7;
}

.type-crane {
  background: #fff3e0;
  color: #ef6c00;
  border: 1px solid #ffcc80;
}

.type-default {
  background: #f5f5f5;
  color: #616161;
  border: 1px solid #e0e0e0;
}

.status-cell {
  text-align: center;
  padding: 6px 8px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-AVAILABLE {
  background: #10b981;
  color: white;
}

.status-IN_USE {
  background: #3b82f6;
  color: white;
}

.calendar-cell {
  border: 1px solid #e5e7eb;
  padding: 2px;
  min-width: 50px;
  max-width: 50px;
  height: 60px;
  vertical-align: top;
  background: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.calendar-cell:hover {
  background: #f3f4f6;
}

.calendar-cell.weekend {
  background: #fef2f2;
}

.calendar-cell.today {
  background: #dbeafe;
}

.cell-content {
  height: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2px;
}

.projects-container {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 100%;
}

.project-chip {
  padding: 3px 6px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.congestion-low .project-chip {
    background: #10b981;
    color: white;
}
.congestion-low .project-chip:hover {
    background: #059669;
}

.congestion-medium {
    background: #fffbe6;
    border: 1px solid #fcd34d;
    border-radius: 4px;
    padding: 2px;
}
.congestion-medium .project-chip {
    background: #f59e0b;
    color: white;
}
.congestion-medium .project-chip:hover {
    background: #d97706;
}

.congestion-high {
    background: #fee2e2;
    border: 1px solid #f87171;
    border-radius: 4px;
    padding: 2px;
}
.congestion-high .project-chip {
    background: #ef4444;
    color: white; 
}
.congestion-high .project-chip:hover {
    background: #dc2626;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  color: #1f2937;
}

.modal-close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close-btn:hover {
  color: #374151;
}

.modal-body {
  padding: 20px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
}

.detail-row:last-child {
  border-bottom: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-close {
  background: #6b7280;
  color: white;
}

.btn-close:hover {
  background: #374151;
}

.btn-retry {
  background: #3b82f6;
  color: white;
}

.btn-retry:hover {
  background: #2563eb;
}

.loading-overlay, .error-message {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner, .error-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.error-content h3 {
  color: #ef4444;
  margin-bottom: 10px;
}

.error-content p {
  color: #6b7280;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .calendar-view {
    padding: 10px;
  }
  
  .calendar-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .header-controls {
    flex-direction: column;
    gap: 15px;
  }
  
  .input-with-button {
    justify-content: space-between;
  }
  
  .days-input, .date-input {
    width: 100px;
  }
  
  .date-header-cell,
  .calendar-cell {
    min-width: 40px;
    max-width: 40px;
    height: 50px;
  }
  
  .col-vehicle {
    min-width: 150px;
  }
  
  .col-type {
    min-width: 100px;
    left: 150px;
  }
  
  .col-status {
    min-width: 100px;
    left: 250px;
  }
  
  .project-chip {
    font-size: 0.6rem;
    padding: 2px 4px;
  }

  .large-popup-container {
    min-width: 95%;
    max-width: 95%;
  }
}

.table-container {
  scroll-behavior: smooth;
}

.vehicle-row:hover .sticky-col {
  background: #f3f4f6;
}

.project-status.active {
  color: #10b981;
  font-weight: 600;
}

.project-status.upcoming {
  color: #f59e0b;
  font-weight: 600;
}

.project-status.completed {
  color: #6b7280;
  font-weight: 600;
}
</style>