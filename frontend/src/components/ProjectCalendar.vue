<template>
  <div class="calendar-view">
    <div class="calendar-header">
      <h2 class="title">Project Calendar</h2>
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
        <!-- New: Show All Projects Button -->
        <button 
          class="show-all-btn" 
          @click="toggleShowAllProjects"
          :class="{ active: showAllProjects }"
        >
          {{ showAllProjects ? 'Show Filtered' : 'Show All' }}
        </button>
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
            <th class="sticky-col col-code" @click="sortBy('code')">
              <div class="header-content">
                Code
                <span class="sort-indicator">{{ sortField === 'code' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
            <th class="sticky-col col-name" @click="sortBy('name')">
              <div class="header-content">
                Project Name
                <span class="sort-indicator">{{ sortField === 'name' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
            <th class="sticky-col col-duration" @click="sortBy('duration')">
              <div class="header-content">
                Days
                <span class="sort-indicator">{{ sortField === 'duration' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
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
            v-for="project in sortedProjects" 
            :key="project.project_id"
            class="project-row"
          >
            <td class="sticky-col col-code" @click="onProjectClick(project)">
              <span class="project-code">{{ project.project_code }}</span>
            </td>
            <td class="sticky-col col-name" @click="onProjectClick(project)">
              <div class="project-info">
                <span class="project-name">{{ project.project_name }}</span>
                <div class="project-date">
                  {{ formatProjectDate(project.date_start) }} - {{ getProjectEndDateFormatted(project) }}
                </div>
              </div>
            </td>
            <td class="sticky-col col-duration" @click="onProjectClick(project)">
              <span class="duration-value">{{ project.duration || 'N/A' }}</span>
            </td>
            <td 
              v-for="date in dateRange" 
              :key="date.getTime()"
              :class="{
                'calendar-cell': true,
                'active': isActiveOnDate(project, date),
                'inactive': !isActiveOnDate(project, date),
                'weekend': isWeekend(date),
                'today': isToday(date),
                'clickable': true
              }"
              @click="onDateClick(project, date)"
            >
              <div class="cell-content"></div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal for Project Date Assignment -->
    <div v-if="showDateAssignmentModal" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Set Project Start Date</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
        <div class="modal-body">
          <p>Set <strong>{{ selectedProject?.project_name }}</strong> to start on:</p>
          <p class="selected-date">{{ formatFullDate(selectedDate) }}</p>
          <div class="modal-actions">
            <button class="btn btn-cancel" @click="closeModals">Cancel</button>
            <button class="btn btn-confirm" @click="updateProjectStartDate">Confirm</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal for Project Resources -->
    <div v-if="showResourcesModal" class="modal-overlay" @click="closeModals">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Project Resources - {{ selectedProject?.project_name }}</h3>
          <button class="modal-close-btn" @click="closeModals">×</button>
        </div>
        <div class="modal-body">
          <div class="resources-section">
            <h4>Assigned Personnel</h4>
            <div v-if="selectedProjectPersonnel.length > 0" class="resources-list">
              <div 
                v-for="person in selectedProjectPersonnel" 
                :key="person.personel_id"
                class="resource-item"
              >
                <span class="resource-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                <span class="resource-role">{{ person.role }}</span>
              </div>
            </div>
            <div v-else class="no-resources">
              No personnel assigned
            </div>
          </div>

          <div class="resources-section">
            <h4>Assigned Vehicles</h4>
            <div v-if="selectedProjectVehicles.length > 0" class="resources-list">
              <div 
                v-for="vehicle in selectedProjectVehicles" 
                :key="vehicle.vehicle_id"
                class="resource-item"
              >
                <span class="resource-name">{{ vehicle.vehicle }}</span>
                <span class="resource-type">{{ vehicle.type }}</span>
              </div>
            </div>
            <div v-else class="no-resources">
              No vehicles assigned
            </div>
          </div>

          <div class="modal-actions">
            <button class="btn btn-personnel" @click="openPersonnelManagement">
              Manage Personnel
            </button>
            <button class="btn btn-vehicles" @click="openVehiclesManagement">
              Manage Vehicles
            </button>
            <button class="btn btn-close" @click="closeModals">Close</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Project Info Component -->
    <div v-if="showProjectInfo" class="modal-overlay" @click="closeModals">
      <div class="modal-content large-modal" @click.stop>
        <ProjectInfo 
          :project="selectedProject"
          :personnel="selectedProjectPersonnel"
          :vehicles="selectedProjectVehicles"
          @close="closeModals"
          @edit-project="handleProjectEdit"
          @edit-personnel="openPersonnelManagement"
          @edit-vehicles="openVehiclesManagement"
          @open-personnel-card="openPersonnelCard"
          @open-vehicle-card="openVehicleCard"
          @project-updated="handleProjectUpdated"
        />
      </div>
    </div>

    <!-- Personnel Management Component -->
    <div v-if="showPersonnelManagement" class="modal-overlay" @click="closeChildModals">
      <div class="modal-content large-modal" @click.stop>
        <AddPersonel 
          :project="selectedProject"
          @close="closeChildModals"
          @personnelUpdated="handleResourcesUpdated"
        />
      </div>
    </div>

    <!-- Vehicles Management Component -->
    <div v-if="showVehiclesManagement" class="modal-overlay" @click="closeChildModals">
      <div class="modal-content large-modal" @click.stop>
        <AddVehicles 
          :project="selectedProject"
          @close="closeChildModals"
          @vehiclesUpdated="handleResourcesUpdated"
        />
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import AddPersonel from "./AddPersonel.vue";
import AddVehicles from "./AddVehicles.vue";
import ProjectInfo from "./ProjectInfo.vue"; // Import ProjectInfo

export default {
  name: "ProjectCalendar",
  components: {
    AddPersonel, 
    AddVehicles,
    ProjectInfo // Add to components
  },
  data() {
    return {
          showProjectInfoBeforeChild: false,
    showResourcesModalBeforeChild: false,
      projects: [],
      calendarDays: 30,
      startDate: new Date().toISOString().split('T')[0],
      dateRange: [],
      sortField: 'name',
      sortDirection: 'asc',
      showAllProjects: false,
      
      // Modal states
      showDateAssignmentModal: false,
      showResourcesModal: false,
      showProjectInfo: false, // Add this
      showPersonnelManagement: false,
      showVehiclesManagement: false,
      
      // Selected items
      selectedProject: null,
      selectedDate: null,
      selectedProjectPersonnel: [],
      selectedProjectVehicles: []
    };
  },
  computed: {
    filteredProjects() {
      if (this.showAllProjects) {
        return this.projects; // Show all projects regardless of date range
      }
      
      return this.projects.filter(project => {
        const start = new Date(project.date_start);
        const end = this.calculateProjectEndDate(project);
        return this.dateRange.some(d => d >= start && d <= end);
      });
    },
    sortedProjects() {
      return [...this.filteredProjects].sort((a, b) => {
        let aValue, bValue;
        
        if (this.sortField === 'code') {
          aValue = a.project_code;
          bValue = b.project_code;
        } else if (this.sortField === 'duration') {
          aValue = a.duration || 0;
          bValue = b.duration || 0;
        } else {
          aValue = a.project_name;
          bValue = b.project_name;
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
  handleProjectUpdated() {
    console.log('Project updated, refreshing data...');
    // Refresh the projects data
    this.getProjects();
    
    // Also refresh the resources for the current project
    if (this.selectedProject) {
      this.loadProjectResources(this.selectedProject.project_id);
    }
  },
    
    async getProjects() {
      try {
        const response = await axios.get("http://localhost:8000/projects");
        this.projects = response.data;
      } catch (error) {
        console.error('Error fetching projects:', error);
      }
    },

    async getProjectPersonnel(projectId) {
      try {
        const response = await axios.get(`http://localhost:8000/projects/${projectId}/personel`);
        return response.data;
      } catch (error) {
        console.error('Error fetching project personnel:', error);
        return [];
      }
    },

    async getProjectVehicles(projectId) {
      try {
        const response = await axios.get(`http://localhost:8000/projects/${projectId}/vehicles`);
        return response.data;
      } catch (error) {
        console.error('Error fetching project vehicles:', error);
        return [];
      }
    },

    async updateProjectStartDate() {
      if (!this.selectedProject || !this.selectedDate) {
        alert('No project or date selected');
        return;
      }

      const projectId = this.selectedProject.project_id;
      const updateData = {
        date_start: new Date(this.selectedDate).toISOString().split('T')[0]
      };

      console.log('PATCH payload:', updateData);

      try {
        const response = await axios.patch(`http://localhost:8000/projects/${projectId}/update`, updateData);
        console.log('Update response:', response.data);
        await this.getProjects();
        this.closeModals();
      } catch (error) {
        console.error('Error updating project start date:', error.response?.data || error.message);
        alert('Failed to update project start date');
      }
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

    calculateProjectEndDate(project) {
      const startDate = new Date(project.date_start);
      const workingDays = project.duration || 0;
      let currentDate = new Date(startDate);
      
      if (workingDays <= 1) {
        return new Date(startDate);
      }
      
      // We already have day 1 (start date), so we need (duration - 1) additional working days
      let additionalDaysNeeded = workingDays - 1;
      let daysAdded = 0;
      
      while (daysAdded < additionalDaysNeeded) {
        currentDate.setDate(currentDate.getDate() + 1);
        if (currentDate.getDay() !== 0 && currentDate.getDay() !== 6) {
          daysAdded++;
        }
      }
      
      return new Date(currentDate);
    },

    getProjectEndDateFormatted(project) {
      const endDate = this.calculateProjectEndDate(project);
      return this.formatProjectDate(endDate);
    },

    formatDateDay(date) {
      if (!(date instanceof Date) || isNaN(date)) return '';
      return date.getDate();
    },

    formatDateWeekday(date) {
      if (!(date instanceof Date) || isNaN(date)) return '';
      return date.toLocaleDateString("en-US", { weekday: "short" });
    },

    formatProjectDate(date) {
      if (!date) return 'N/A';
      const dateObj = date instanceof Date ? date : new Date(date);
      if (!(dateObj instanceof Date) || isNaN(dateObj)) {
        return 'N/A';
      }
      return dateObj.toLocaleDateString("en-US", { 
        month: "short", 
        day: "numeric"
      });
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

    isActiveOnDate(project, date) {
      if (!(date instanceof Date) || isNaN(date)) return false;
      const start = new Date(project.date_start);
      const end = this.calculateProjectEndDate(project);
      return date >= start && date <= end && !this.isWeekend(date);
    },

    goToToday() {
      this.startDate = new Date().toISOString().split('T')[0];
      this.generateDates();
    },

    toggleShowAllProjects() {
      this.showAllProjects = !this.showAllProjects;
    },

    sortBy(field) {
      if (this.sortField === field) {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortField = field;
        this.sortDirection = 'asc';
      }
    },

    async onDateClick(project, date) {
      this.selectedProject = project;
      this.selectedDate = date;
      
      const isActive = this.isActiveOnDate(project, date);
      
      if (isActive) {
        // Clicked on active (green) square - show resources
        await this.loadProjectResources(project.project_id);
        this.showResourcesModal = true;
      } else {
        // Clicked on inactive square - show date assignment
        this.showDateAssignmentModal = true;
      }
    },

    async onProjectClick(project) {
      this.selectedProject = project;
      
      // Load resources for the project info
      await this.loadProjectResources(project.project_id);
      
      this.showProjectInfo = true;
    },

    async loadProjectResources(projectId) {
      try {
        const [personnel, vehicles] = await Promise.all([
          this.getProjectPersonnel(projectId),
          this.getProjectVehicles(projectId)
        ]);
        this.selectedProjectPersonnel = personnel;
        this.selectedProjectVehicles = vehicles;
      } catch (error) {
        console.error('Error loading project resources:', error);
      }
    },

  openPersonnelManagement() {
    // Track which modal we're coming from
    this.showProjectInfoBeforeChild = this.showProjectInfo;
    this.showResourcesModalBeforeChild = this.showResourcesModal;
    
    this.showResourcesModal = false;
    this.showProjectInfo = false;
    this.showPersonnelManagement = true;
  },

  openVehiclesManagement() {
    // Track which modal we're coming from
    this.showProjectInfoBeforeChild = this.showProjectInfo;
    this.showResourcesModalBeforeChild = this.showResourcesModal;
    
    this.showResourcesModal = false;
    this.showProjectInfo = false;
    this.showVehiclesManagement = true;
  },

    handleProjectEdit(project) {
      console.log('Edit project requested:', project);
      // You can implement project editing logic here
      // For now, just close and reopen the project info
      this.showProjectInfo = false;
      setTimeout(() => {
        this.selectedProject = project;
        this.showProjectInfo = true;
      }, 100);
    },

    openPersonnelCard(person, project) {
      console.log('Open personnel card:', person, project);
      // Implement personnel card view if needed
    },

    openVehicleCard(vehicle, project) {
      console.log('Open vehicle card:', vehicle, project);
      // Implement vehicle card view if needed
    },

  handleResourcesUpdated() {
    // Refresh resources data
    if (this.selectedProject) {
      this.loadProjectResources(this.selectedProject.project_id);
    }
  },

    closeModals() {
      // Close all modals completely
      this.showDateAssignmentModal = false;
      this.showResourcesModal = false;
      this.showProjectInfo = false;
      this.showPersonnelManagement = false;
      this.showVehiclesManagement = false;
      this.selectedProject = null;
      this.selectedDate = null;
      this.selectedProjectPersonnel = [];
      this.selectedProjectVehicles = [];
    },

 closeChildModals() {
    this.showPersonnelManagement = false;
    this.showVehiclesManagement = false;
    
    // Refresh data when returning to ProjectInfo
    if (this.selectedProject) {
      this.loadProjectResources(this.selectedProject.project_id);
    }
    
    // Re-open the appropriate parent modal
    if (this.showProjectInfoBeforeChild) {
      this.showProjectInfo = true;
      this.showProjectInfoBeforeChild = false;
    } 
    if (this.showResourcesModalBeforeChild) {
      this.showResourcesModal = true;
      this.showResourcesModalBeforeChild = false;
    }
  },

  },
  mounted() {
    this.getProjects();
    this.generateDates();
  }
};
</script>

<style scoped>
/* Add new button styles */
.show-all-btn {
  padding: 6px 12px;
  background: #6b7280;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.show-all-btn:hover {
  background: #4b5563;
}

.show-all-btn.active {
  background: #059669;
}

.show-all-btn.active:hover {
  background: #047857;
}

/* Modal styles */
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
  padding: 20px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

/* .large-modal {
  max-width: 700px;
  width: 50%;
  padding: 0;
} */

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 15px;
}

.modal-header h3 {
  margin: 0;
  color: #1f2937;
}

.modal-close-btn {
  background: none;
  border: none;
  font-size: 24px;
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
  margin-bottom: 20px;
}

.selected-date {
  font-size: 1.1rem;
  font-weight: 600;
  color: #059669;
  text-align: center;
  margin: 15px 0;
  padding: 10px;
  background: #f0fdf4;
  border-radius: 4px;
}

.resources-section {
  margin-bottom: 25px;
}

.resources-section h4 {
  margin: 0 0 10px 0;
  color: #374151;
  font-size: 1rem;
}

.resources-list {
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  padding: 10px;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
}

.resource-item:last-child {
  border-bottom: none;
}

.resource-name {
  font-weight: 500;
  color: #1f2937;
}

.resource-role, .resource-type {
  color: #6b7280;
  font-size: 0.8rem;
}

.no-resources {
  color: #9ca3af;
  font-style: italic;
  text-align: center;
  padding: 20px;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-wrap: wrap;
  margin-top: 20px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-cancel {
  background: #6b7280;
  color: white;
}

.btn-cancel:hover {
  background: #4b5563;
}

.btn-confirm {
  background: #059669;
  color: white;
}

.btn-confirm:hover {
  background: #047857;
}

.btn-personnel {
  background: #3b82f6;
  color: white;
}

.btn-personnel:hover {
  background: #2563eb;
}

.btn-vehicles {
  background: #8b5cf6;
  color: white;
}

.btn-vehicles:hover {
  background: #7c3aed;
}

.btn-close {
  background: #6b7280;
  color: white;
}

.btn-close:hover {
  background: #4b5563;
}

/* Keep all your existing styles below */
.calendar-view {
  max-width: 100%;
  padding: 24px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #f8fafc;
  min-height: 100vh;
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
  gap: 16px;
  align-items: center;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.control-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #4a5568;
}

.input-with-button {
  display: flex;
  gap: 4px;
  align-items: center;
}

.days-input, .date-input {
  padding: 6px 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.8rem;
  transition: all 0.2s ease;
  width: 70px;
  background: white;
}

.days-input:focus, .date-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.refresh-btn, .today-btn {
  padding: 6px 12px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.refresh-btn:hover, .today-btn:hover {
  background: #2563eb;
}

.table-container {
  max-width: 100%;
  overflow-x: auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: relative;
  border: 1px solid #e2e8f0;
}

.calendar-table {
  width: max-content;
  border-collapse: collapse;
  min-width: 100%;
}

/* Month Header Styles */
.month-header {
  position: sticky;
  top: 0;
  z-index: 40;
}

.month-header-cell {
  background: #374151;
  color: white;
  font-weight: 600;
  font-size: 0.8rem;
  padding: 10px 4px;
  text-align: center;
  border-left: 1px solid #4b5563;
}

.month-col {
  background: #1f2937;
  color: white;
  font-weight: 600;
  font-size: 0.8rem;
  padding: 10px;
  text-align: center;
}

/* Date Header Styles */
.date-header {
  position: sticky;
  top: 39px; /* Height of month header */
  z-index: 35;
}

.date-header-cell {
  background: #f9fafb;
  font-weight: 500;
  border-left: 1px solid #e5e7eb;
  border-bottom: 2px solid #e5e7eb;
  padding: 6px 4px;
  text-align: center;
  font-size: 0.7rem;
  min-width: 40px;
  max-width: 40px;
  height: 45px;
  box-sizing: border-box;
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
  gap: 1px;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.date-day {
  font-size: 0.8rem;
  font-weight: 600;
}

.date-weekday {
  font-size: 0.6rem;
  opacity: 0.8;
  text-transform: uppercase;
}

/* Sticky Columns - Fixed width and professional styling */
.sticky-col {
  position: sticky;
  left: 0;
  background: #f9fafb;
  z-index: 30;
  border-right: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  width: 120px; /* Fixed width */
}

.col-code {
  min-width: 120px;
  max-width: 120px;
  left: 0;
  z-index: 31;
}

.col-name {
  min-width: 250px;
  max-width: 250px;
  left: 120px;
  z-index: 32;
}

.col-duration {
  min-width: 80px;
  max-width: 80px;
  left: 370px;
  z-index: 33;
}

/* Interactive sticky columns */
.sticky-col:hover {
  background: #f3f4f6;
  cursor: pointer;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 8px;
  font-weight: 600;
  font-size: 0.75rem;
  color: #374151;
}

.sort-indicator {
  font-size: 0.7rem;
  color: #6b7280;
}

/* Body cells */
.calendar-table tbody td {
  padding: 4px;
  text-align: center;
  font-size: 0.7rem;
  min-width: 40px;
  max-width: 40px;
  transition: all 0.2s ease;
  border-bottom: 1px solid #e5e7eb;
  height: 35px;
  box-sizing: border-box;
  border-left: 1px solid #e5e7eb;
}

/* Project Rows */
.project-row:hover {
  background: #f9fafb !important;
}

.project-row:hover .sticky-col {
  background: #f3f4f6;
}

/* Project code styling */
.project-code {
  font-family: 'Monaco', 'Consolas', monospace;
  font-weight: 600;
  color: #1f2937;
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  display: inline-block;
  transition: all 0.2s ease;
}

.sticky-col:hover .project-code {
  background: #e5e7eb;
}

/* Project info styling */
.project-info {
  text-align: left;
  padding: 0 8px;
}

.project-name {
  font-weight: 500;
  color: #1f2937;
  display: block;
  font-size: 0.8rem;
  line-height: 1.3;
  margin-bottom: 2px;
}

.project-date {
  font-size: 0.7rem;
  color: #6b7280;
}

/* Duration styling - Simple */
.duration-value {
  font-size: 0.8rem;
  color: #374151;
  font-weight: 600;
}

/* Calendar Cells */
.calendar-cell {
  position: relative;
  background: #ffffff;
  border-left: 1px solid #e5e7eb;
  padding: 0;
}

.calendar-cell.active {
  background: #10b981;
  position: relative;
}

.calendar-cell.active.clickable {
  cursor: pointer;
}

.calendar-cell.active.clickable:hover {
  background: #059669;
  z-index: 10;
}

.calendar-cell.inactive {
  background: #f9fafb;
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
  align-items: center;
  justify-content: center;
}

/* Alternating row colors */
.calendar-table tbody tr:nth-child(even) {
  background: #fafafa;
}

.calendar-table tbody tr:nth-child(even) .sticky-col {
  background: #f3f4f6;
}

/* Responsive Design */
@media (max-width: 768px) {
  .calendar-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .header-controls {
    justify-content: space-between;
  }
  
  .sticky-col {
    width: 100px;
  }
  
  .col-code {
    min-width: 100px;
    max-width: 100px;
  }
  
  .col-name {
    min-width: 180px;
    max-width: 180px;
    left: 100px;
  }
  
  .col-duration {
    min-width: 60px;
    max-width: 60px;
    left: 280px;
  }
  
  .calendar-table th,
  .calendar-table td {
    min-width: 35px;
    max-width: 35px;
    padding: 2px;
    font-size: 0.65rem;
  }
  
  .month-header-cell,
  .month-col {
    font-size: 0.7rem;
    padding: 6px 2px;
  }

  .modal-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}

/* Smooth scrolling */
.table-container {
  scroll-behavior: smooth;
}
</style>