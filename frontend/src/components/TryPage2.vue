<template>
  <div class="calendar-view">
    <div class="calendar-header">
      <h2 class="title">Personnel Calendar</h2>
      <div class="header-controls">
        <!-- Search Bar -->
        <div class="control-group search-group">
          <label class="control-label">Search:</label>
          <input 
            type="text" 
            v-model="searchQuery"
            placeholder="Search by name, role, project..."
            class="search-input"
          />
        </div>
        
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
            <th class="sticky-col col-name fixed-column" @click="sortBy('name')">
              <div class="header-content">
                Name
                <span class="sort-indicator">{{ sortField === 'name' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
            <th class="sticky-col col-role fixed-column" @click="sortBy('role')">
              <div class="header-content">
                Role
                <span class="sort-indicator">{{ sortField === 'role' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
              </div>
            </th>
            <th class="sticky-col col-status fixed-column" @click="sortBy('status')">
              <div class="header-content">
                Status
                <span class="sort-indicator">{{ sortField === 'status' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}</span>
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
            v-for="person in filteredAndSortedPersonnel" 
            :key="person.personel_id"
            class="personnel-row"
          >
            <td class="sticky-col col-name">
              <div class="personnel-info clickable" @click="openPersonelInfo(person)">
                <span class="personnel-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
              </div>
            </td>
            <td class="sticky-col col-role">
              <div :class="['role-cell', getRoleClass(person.role)]">
                <span class="role-text">{{ person.role }}</span>
              </div>
            </td>
            <td class="sticky-col col-status">
              <div :class="['status-cell', `status-${getValidityStatus(person)}`]">
                <span class="status-text">{{ getValidityStatus(person) }}</span>
              </div>
            </td>
            <td 
              v-for="date in dateRange" 
              :key="date.getTime()"
              :class="{
                'calendar-cell': true,
                'weekend': isWeekend(date),
                'today': isToday(date),
                'clickable': true
              }"
              @click="onDateClick(person, date)"
            >
              <div class="cell-content">
                <!-- CASE 1: Personnel has assigned projects -->
                <div 
                  v-if="getProjectsOnDate(person, date).length > 0"
                  class="projects-container"
                  :class="getProjectCountClass(getProjectsOnDate(person, date).length)"
                >
                  <div 
                    v-for="project in getProjectsOnDate(person, date)" 
                    :key="project.project_id"
                    class="project-chip"
                    @click.stop="onProjectClick(project)"
                    :title="getProjectTooltip(project)"
                  >
                    {{ project.project_code }}
                  </div>
                </div>

                <!-- CASE 2: No assigned projects for this person on this date -->
                <div
                  v-else
                  class="no-projects-container"
                >
                  <div 
                    v-for="project in getActiveProjectsOnDate(date)"
                    :key="project.project_id"
                    class="project-chip"
                    :class="getProjectDisplayClass(project)"
                    @click.stop="onProjectClick(project)"
                    :title="getProjectTooltip(project)"
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

    <!-- Project Info Modal -->
    <div v-if="showProjectInfo" class="modal-overlay project-info-overlay" @click="closeProjectInfo">
      <div class="modal-content project-info-modal" @click.stop>
        <ProjectInfo
          v-if="selectedProject"
          :project="selectedProject"
          :personnel="selectedProjectPersonnel"
          :vehicles="selectedProjectVehicles"
          @close="closeProjectInfo"
          @project-updated="handleProjectUpdated"
          @edit-personnel="handleEditPersonnel"
          @edit-vehicles="handleEditVehicles"
          @open-personnel-card="openPersonelInfo"
        />
      </div>
    </div>

    <!-- Personel Info Modal -->
    <div v-if="showPersonelInfo" class="modal-overlay personel-info-overlay" @click="closePersonelInfo">
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

    <!-- Add Personnel Modal -->
    <div v-if="showAddPersonnel" class="modal-overlay" @click="showAddPersonnel = false">
      <div class="modal-content" @click.stop>
        <AddPersonel 
          :project="selectedProjectForEdit"
          @close="showAddPersonnel = false"
          @personnelUpdated="handleProjectUpdated"
        />
      </div>
    </div>

    <!-- Add Vehicles Modal -->
    <div v-if="showAddVehicles" class="modal-overlay" @click="showAddVehicles = false">
      <div class="modal-content" @click.stop>
        <AddVehicles 
          :project="selectedProjectForEdit"
          @close="showAddVehicles = false"
          @vehiclesUpdated="handleProjectUpdated"
        />
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import ProjectInfo from "./ProjectInfo.vue";
import PersonelInfo from "./PersonelInfo.vue";
import AddPersonel from "./AddPersonel.vue";
import AddVehicles from "./AddVehicles.vue";

export default {
  name: "PersonnelCalendar",
  components: {
    ProjectInfo,
    PersonelInfo,
    AddPersonel,
    AddVehicles
  },
  data() {
    return {
      personnel: [],
      projects: [],
      calendarDays: 30,
      startDate: new Date().toISOString().split('T')[0],
      dateRange: [],
      sortField: 'name',
      sortDirection: 'asc',
      searchQuery: '',
      
      // Project Info states
      showProjectInfo: false,
      selectedProject: null,
      selectedProjectPersonnel: [],
      selectedProjectVehicles: [],
      
      // Personel Info states
      showPersonelInfo: false,
      selectedPersonnel: null,
      loadingPersonnel: false,
      
      // Edit modals
      showAddPersonnel: false,
      showAddVehicles: false,
      selectedProjectForEdit: null
    };
  },
  computed: {
    filteredAndSortedPersonnel() {
      let filtered = this.personnel;
      
      // Apply search filter
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(person => 
          `${person.personel_name} ${person.personel_surname}`.toLowerCase().includes(query) ||
          person.role.toLowerCase().includes(query) ||
          this.doesPersonHaveProjectsMatchingQuery(person, query)
        );
      }
      
      // Apply sorting
      return [...filtered].sort((a, b) => {
        let aValue, bValue;
        
        if (this.sortField === 'role') {
          aValue = a.role;
          bValue = b.role;
        } else if (this.sortField === 'status') {
          aValue = this.getValidityStatus(a);
          bValue = this.getValidityStatus(b);
        } else {
          aValue = `${a.personel_name} ${a.personel_surname}`.toLowerCase();
          bValue = `${b.personel_name} ${b.personel_surname}`.toLowerCase();
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
    // Search helper
    doesPersonHaveProjectsMatchingQuery(person, query) {
      if (!person.projects) return false;
      return person.projects.some(project => 
        project.project_code.toLowerCase().includes(query) ||
        project.project_name.toLowerCase().includes(query)
      );
    },

    async getPersonnel() {
      try {
        const response = await axios.get("http://localhost:8000/personel");
        this.personnel = response.data;
        await this.loadProjectsForAllPersonnel();
      } catch (error) {
        console.error('Error fetching personnel:', error);
      }
    },

    async getProjects() {
      try {
        const response = await axios.get("http://localhost:8000/projects");
        this.projects = response.data || [];
        await this.enrichProjectsWithPersonnelData();
      } catch (error) {
        console.error('Error fetching projects:', error);
      }
    },

    async enrichProjectsWithPersonnelData() {
      const promises = this.projects.map(async (project) => {
        try {
          const res = await axios.get(`http://localhost:8000/projects/${project.project_id}/personel`);
          project.assigned_personel = Array.isArray(res.data) ? res.data.length : 0;
        } catch (err) {
          console.error(`Failed to fetch personnel for project ${project.project_id}`, err);
          project.assigned_personel = 0;
        }
      });
      await Promise.all(promises);
    },

    async loadProjectsForAllPersonnel() {
      try {
        const promises = this.personnel.map(async (person) => {
          try {
            const response = await axios.get(`http://localhost:8000/personel/${person.personel_id}/projects`);
            person.projects = response.data;
          } catch (error) {
            console.error(`Failed to load projects for personnel ${person.personel_id}`, error);
            person.projects = [];
          }
        });
        await Promise.all(promises);
      } catch (error) {
        console.error("Failed to load projects data", error);
      }
    },

    getProjectsOnDate(person, date) {
      if (!person.projects || person.projects.length === 0) return [];
      
      return person.projects.filter(project => {
        const startDate = new Date(project.date_start);
        const endDate = this.calculateProjectEndDate(project);
        return date >= startDate && date <= endDate && !this.isWeekend(date);
      });
    },

    getActiveProjectsOnDate(date) {
      if (!this.projects || this.projects.length === 0) return [];
      return this.projects.filter(project => {
        const startDate = new Date(project.date_start);
        const endDate = this.calculateProjectEndDate(project);
        return date >= startDate && date <= endDate && !this.isWeekend(date);
      });
    },

    getProjectDisplayClass(project) {
      const needsPersonnel = (project.assigned_personel || 0) < (project.expected_personel || 0);
      return needsPersonnel ? 'project-chip-need' : 'project-chip-ok';
    },

    getProjectTooltip(project) {
      const assigned = project.assigned_personel || 0;
      const expected = project.expected_personel || 'N/A';
      const status = (assigned < expected) ? 'Needs personnel' : 'Fully staffed';
      return `${project.project_code} — ${project.project_name}\nAssigned: ${assigned}/${expected}\nStatus: ${status}`;
    },

    calculateProjectEndDate(project) {
      const startDate = new Date(project.date_start);
      const workingDays = project.duration || 0;
      let currentDate = new Date(startDate);
      
      if (workingDays <= 1) return new Date(startDate);
      
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

    getProjectCountClass(count) {
      if (count === 1) return 'single-project';
      if (count <= 3) return 'multiple-projects';
      return 'many-projects';
    },

    // Date methods
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

    onDateClick(person, date) {
      const projectsOnDate = this.getProjectsOnDate(person, date);
      if (projectsOnDate.length > 0) {
        console.log(`Projects for ${person.personel_name} on ${date}:`, projectsOnDate);
      }
    },

    async onProjectClick(project) {
      try {
        this.selectedProject = project;
        
        // Fetch assigned personnel
        const personnelResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}/personel`);
        this.selectedProjectPersonnel = personnelResponse.data;
        
        // Fetch assigned vehicles
        const vehiclesResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}/vehicles`);
        this.selectedProjectVehicles = vehiclesResponse.data;
        
        this.showProjectInfo = true;
        document.body.style.overflow = 'hidden';
      } catch (error) {
        console.error('Error fetching project details:', error);
        this.selectedProjectPersonnel = [];
        this.selectedProjectVehicles = [];
        this.showProjectInfo = true;
        document.body.style.overflow = 'hidden';
      }
    },

    closeProjectInfo() {
      this.showProjectInfo = false;
      this.selectedProject = null;
      this.selectedProjectPersonnel = [];
      this.selectedProjectVehicles = [];
      document.body.style.overflow = '';
    },

    // Personel Info methods
    async openPersonelInfo(person) {
      try {
        this.loadingPersonnel = true;
        const response = await axios.get(`http://localhost:8000/personel/${person.personel_id}`);
        this.selectedPersonnel = response.data;
        this.showPersonelInfo = true;
        document.body.style.overflow = 'hidden';
      } catch (error) {
        console.error("Failed to load personnel data", error);
        alert('Failed to load personnel details. Please try again.');
      } finally {
        this.loadingPersonnel = false;
      }
    },

    closePersonelInfo() {
      this.showPersonelInfo = false;
      this.selectedPersonnel = null;
      document.body.style.overflow = '';
    },

    openProjectCardFromPersonel(project) {
      this.selectedProject = project;
      this.showProjectInfo = true;
      this.closePersonelInfo();
    },

    // Event handlers
    handleProjectUpdated() {
      this.getProjects();
      this.loadProjectsForAllPersonnel();
      this.closeProjectInfo();
    },

    async handlePersonelUpdated() {
      await this.refreshPersonnelData();
      if (this.selectedPersonnel) {
        const freshData = await this.fetchCompletePersonnelData(this.selectedPersonnel.personel_id);
        if (freshData) this.selectedPersonnel = freshData;
      }
    },

    async refreshPersonnelData() {
      try {
        const response = await axios.get("http://localhost:8000/personel");
        this.personnel = response.data;
        await this.loadProjectsForAllPersonnel();
      } catch (error) {
        console.error('Error refreshing personnel data:', error);
      }
    },

    async fetchCompletePersonnelData(personelId) {
      try {
        const response = await axios.get(`http://localhost:8000/personel/${personelId}`);
        return response.data;
      } catch (error) {
        console.error("Failed to load personnel data", error);
        return null;
      }
    },

    handleEditPersonnel(project) {
      this.selectedProjectForEdit = project;
      this.showAddPersonnel = true;
    },

    handleEditVehicles(project) {
      this.selectedProjectForEdit = project;
      this.showAddVehicles = true;
    },

    // Medical, Xray, Education event handlers
    async handleMedicalCreated() { await this.refreshPersonnelData(); },
    async handleXrayCreated() { await this.refreshPersonnelData(); },
    async handleEducationCreated() { await this.refreshPersonnelData(); },
    async handleMedicalRemoved() { await this.refreshPersonnelData(); },
    async handleXrayRemoved() { await this.refreshPersonnelData(); },
    async handleEducationRemoved() { await this.refreshPersonnelData(); },

    // Role and status methods
    getRoleClass(role) {
      const roleMap = {
        'Engineer': 'role-engineer',
        'Worker': 'role-worker',
        'Driver': 'role-driver',
        'Manager': 'role-manager',
        'Technician': 'role-technician'
      };
      return roleMap[role] || 'role-default';
    },

    getValidityStatus(person) {
      const medicalExpiry = this.getMedicalExpiration(person);
      const educationExpiry = this.getEducationExpiration(person);
      const xrayExpiry = this.getXrayExpiration(person);

      if (!medicalExpiry || !educationExpiry || !xrayExpiry) {
        return 'INVALID';
      }

      const today = new Date();
      let allValid = true;
      let anyAlmost = false;

      [medicalExpiry, educationExpiry, xrayExpiry].forEach(date => {
        const expiryDate = new Date(date);
        const diffTime = expiryDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        if (diffDays < 0) {
          allValid = false;
        } else if (diffDays <= 60) {
          anyAlmost = true;
          allValid = false;
        }
      });

      if (allValid) return 'VALID';
      if (anyAlmost) return 'ALMOST';
      return 'INVALID';
    },

    getMedicalExpiration(person) {
      if (!person.medicals || person.medicals.length === 0) return null;
      const latestMedical = person.medicals.reduce((latest, current) => {
        return new Date(current.exam_date) > new Date(latest.exam_date) ? current : latest;
      });
      const examDate = new Date(latestMedical.exam_date);
      examDate.setFullYear(examDate.getFullYear() + 1);
      return examDate.toISOString().split('T')[0];
    },

    getEducationExpiration(person) {
      if (!person.education || person.education.length === 0) return null;
      const latestEducation = person.education.reduce((latest, current) => {
        return new Date(current.education_date) > new Date(latest.education_date) ? current : latest;
      });
      const educationDate = new Date(latestEducation.education_date);
      educationDate.setFullYear(educationDate.getFullYear() + 3);
      return educationDate.toISOString().split('T')[0];
    },

    getXrayExpiration(person) {
      if (!person.xrays || person.xrays.length === 0) return null;
      const latestXray = person.xrays.reduce((latest, current) => {
        return new Date(current.xrays_date) > new Date(latest.xrays_date) ? current : latest;
      });
      const xrayDate = new Date(latestXray.xrays_date);
      xrayDate.setFullYear(xrayDate.getFullYear() + 2);
      return xrayDate.toISOString().split('T')[0];
    }
  },
  mounted() {
    this.getPersonnel();
    this.getProjects();
    this.generateDates();
  }
};
</script>

<style scoped>
/* Reuse calendar styles from projects */
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
  flex-wrap: wrap;
  gap: 16px;
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
  flex-wrap: wrap;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.search-group {
  min-width: 200px;
}

.search-input {
  padding: 6px 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.8rem;
  transition: all 0.2s ease;
  background: white;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
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

/* Sticky Columns - Adjusted for personnel */
.sticky-col {
  position: sticky;
  left: 0;
  background: #f9fafb;
  z-index: 30;
  border-right: 1px solid #e5e7eb;
  transition: all 0.2s ease;
}

.col-name {
  min-width: 180px;
  max-width: 180px;
  left: 0;
  z-index: 31;
}

.col-role {
  min-width: 120px;
  max-width: 120px;
  left: 180px;
  z-index: 32;
}

.col-status {
  min-width: 100px;
  max-width: 100px;
  left: 300px;
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

/* Personnel Rows */
.personnel-row:hover {
  background: #f9fafb !important;
}

.personnel-row:hover .sticky-col {
  background: #f3f4f6;
}

/* Personnel info styling */
.personnel-info {
  text-align: left;
  padding: 0 8px;
}

.personnel-info.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 4px;
  padding: 4px 8px;
  margin: -4px -8px;
}

.personnel-info.clickable:hover {
  background: #e3f2fd;
  transform: translateY(-1px);
}

.personnel-name {
  font-weight: 500;
  color: #1f2937;
  display: block;
  font-size: 0.8rem;
  line-height: 1.3;
}

/* Role Cell Styling */
.role-cell {
  text-align: center;
  padding: 4px 2px;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-height: 25px;
  justify-content: center;
  align-items: center;
  border: 1px solid transparent;
  font-size: 0.7rem;
  font-weight: 600;
}

.role-engineer {
  background: #e3f2fd;
  color: #1976d2;
  border-color: #90caf9;
}

.role-worker {
  background: #e8f5e8;
  color: #2e7d32;
  border-color: #a5d6a7;
}

.role-driver {
  background: #fff3e0;
  color: #ef6c00;
  border-color: #ffcc80;
}

.role-manager {
  background: #f3e5f5;
  color: #7b1fa2;
  border-color: #ce93d8;
}

.role-technician {
  background: #e0f2f1;
  color: #00695c;
  border-color: #80cbc4;
}

.role-default {
  background: #f5f5f5;
  color: #616161;
  border-color: #e0e0e0;
}

.role-text {
  font-size: 0.65rem;
  font-weight: 600;
}

/* Status Cell Styling */
.status-cell {
  text-align: center;
  padding: 4px 2px;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-height: 25px;
  justify-content: center;
  align-items: center;
  border: 1px solid transparent;
  font-size: 0.65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-text {
  font-size: 0.6rem;
}

.status-VALID {
  background-color: #10b981;
  color: white;
  border: 1px solid #059669;
}

.status-ALMOST {
  background-color: #f59e0b;
  color: white;
  border: 1px solid #d97706;
}

.status-INVALID {
  background-color: #ef4444;
  color: white;
  border: 1px solid #dc2626;
}

/* Calendar Cells */
.calendar-cell {
  position: relative;
  background: #ffffff;
  border-left: 1px solid #e5e7eb;
  padding: 0;
}

.calendar-cell.weekend {
  background: #fef2f2;
}

.calendar-cell.today {
  background: #dbeafe;
}

.calendar-cell.clickable {
  cursor: pointer;
}

.calendar-cell.clickable:hover {
  background: #f3f4f6;
  z-index: 10;
}

.cell-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1px;
}

/* Projects Container */
.projects-container {
  display: flex;
  flex-direction: column;
  gap: 1px;
  width: 100%;
  height: 100%;
  padding: 1px;
  border-radius: 2px;
}

.projects-container.single-project {
  background: #10b981; /* Green for single project */
}

.projects-container.multiple-projects {
  background: #f59e0b; /* Yellow for 2-3 projects */
}

.projects-container.many-projects {
  background: #ef4444; /* Red for more than 3 projects */
}

table td, table th {
  min-width: 70px !important;
}

.fixed-column {
  position: sticky;
  left: 0;
  background: white;
  z-index: 2;
}

.fixed-column:nth-child(1) { width: 180px; left: 0; }
.fixed-column:nth-child(2) { width: 120px; left: 180px; }
.fixed-column:nth-child(3) { width: 100px; left: 300px; }

/* NO-PROJECTS container (light grey background for the cell) */
.no-projects-container {
  background: #e5e7eb; /* light grey */
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  height: 100%;
  padding: 4px;
  border-radius: 2px;
  align-items: center;
  justify-content: center;
  min-height: 35px;
}

/* Project Chips */
.project-chip {
  font-size: 0.55rem;
  font-weight: 700;
  color: white;
  padding: 2px 4px;
  border-radius: 3px;
  cursor: pointer;
  text-align: center;
  line-height: 1;
  transition: all 0.15s ease;
  border: 1px solid rgba(255, 255, 255, 0.15);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.project-chip:hover {
  transform: scale(1.03);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* Project chip when project has enough personnel */
.project-chip-ok {
  background: #6b7280; /* neutral grey hue */
  color: white;
  border: 1px solid rgba(255,255,255,0.1);
}

/* Project chip when project needs more personnel (soft red) */
.project-chip-need {
  background: #f87171; /* soft red hue */
  color: white;
  border: 1px solid rgba(255,255,255,0.1);
}

/* Alternating row colors */
.calendar-table tbody tr:nth-child(even) {
  background: #fafafa;
}

.calendar-table tbody tr:nth-child(even) .sticky-col {
  background: #f3f4f6;
}

/* Modal Styles */
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
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.25s ease;
}

.project-info-overlay {
  z-index: 1000;
}

.personel-info-overlay {
  z-index: 1001;
}

.personel-info-modal {
  max-width: 500px;
  width: 95%;
  padding: 0;
  background: transparent;
}

.personel-info-modal ::v-deep .personel-info-card {
  max-width: none;
  margin: 0;
  height: 100%;
  max-height: 90vh;
  overflow-y: auto;
  overflow-x: hidden;
}

.personel-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #6c757d;
  background: white;
  border-radius: 12px;
  height: 200px;
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
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
    width: auto;
  }
  
  .col-name {
    min-width: 140px;
    max-width: 140px;
  }
  
  .col-role {
    min-width: 100px;
    max-width: 100px;
    left: 140px;
  }
  
  .col-status {
    min-width: 80px;
    max-width: 80px;
    left: 240px;
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

  .project-chip {
    font-size: 0.5rem;
    padding: 1px 2px;
  }
}

/* Smooth scrolling */
.table-container {
  scroll-behavior: smooth;
}
</style>