<template>
  <div class="project-info-card">
    <!-- Header Section -->
    <div class="project-info-header">
      <div class="project-info-title">
        <h2 class="project-info-code">{{ project.project_code }}</h2>
        <h1 class="project-info-name">{{ project.project_name }}</h1>
        <div class="project-info-daterange">
          {{ formattedDateRange }}
        </div>
      </div>
      <div class="project-info-actions">
        <button class="project-info-btn project-info-btn-close" @click="handleClose">
          ×
        </button>
        <button class="project-info-btn project-info-btn-edit" @click="handleProjectEdit">
          EDIT PROJECT
        </button>
      </div>
    </div>

    <!-- Basic Info Section -->
    <div class="project-info-section project-info-compact">
      <div class="project-info-grid">
        <div class="project-info-item">
          <label>Location</label>
          <span class="project-info-location">{{ project.location || 'Not specified' }}</span>
        </div>
        <div class="project-info-item">
          <label>Duration</label>
          <div class="project-info-duration">
            <span class="project-info-duration-value">{{ project.duration || 0 }}</span>
            <span class="project-info-duration-unit">days</span>
          </div>
        </div>
        <div class="project-info-item">
          <label>Crane</label>
          <span :class="['project-info-crane', project.crane === 'Yes' ? 'project-info-crane-yes' : 'project-info-crane-no']">
            {{ project.crane === 'Yes' ? '✓ Yes' : '✗ No' }}
          </span>
        </div>
        <div class="project-info-item">
          <label>Personnel</label>
          <div class="project-info-personnel-stats">
            <span 
              :class="['project-info-personnel-count', personnelStatus]"
              :title="`${assignedPersonnelCount} assigned / ${project.expected_personel || 0} expected`"
            >
              {{ assignedPersonnelCount }}/{{ project.expected_personel || 0 }}
            </span>
          </div>
        </div>
        <div class="project-info-item">
          <label>Vehicles</label>
          <div class="project-info-vehicles-stats">
            <span class="project-info-vehicles-count">
              {{ assignedVehiclesCount }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Map Section -->
    <div class="project-info-section project-info-compact" v-if="project.xy_map">
      <div class="project-info-section-header">
        <h3>Location Map</h3>
        <a 
          :href="mapsLink" 
          target="_blank" 
          class="project-info-map-link"
        >
          Open in Maps ↗
        </a>
      </div>
      <div class="project-info-map" ref="mapContainer">
        <div class="project-info-map-placeholder" v-if="!mapLoaded">
          <span class="project-info-map-icon">🗺️</span>
          <span class="project-info-map-coords">{{ project.xy_map }}</span>
          <button class="project-info-btn project-info-btn-map" @click="loadMap">Load Map</button>
        </div>
      </div>
    </div>

    <!-- Assigned Personnel Section -->
    <div class="project-info-section project-info-compact">
      <div class="project-info-section-header">
        <h3>Personnel ({{ assignedPersonnelCount }})</h3>
        <button class="project-info-btn project-info-btn-small" @click="handlePersonnelEdit">
          Edit-Personnel
        </button>
      </div>
      <div class="project-info-grid-cards">
        <div 
          v-for="person in personnel" 
          :key="person.personel_id"
          class="project-info-card-item project-info-clickable"
          @click="openPersonnelCard(person)"
        >
          <div class="project-info-card-content">
            <span class="project-info-card-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
            <span class="project-info-card-detail">{{ person.role }}</span>
          </div>
        </div>
        <div v-if="!personnel || personnel.length === 0" class="project-info-empty">
          No personnel assigned
        </div>
      </div>
    </div>

    <!-- Assigned Vehicles Section -->
    <div class="project-info-section project-info-compact">
      <div class="project-info-section-header">
        <h3>Vehicles ({{ assignedVehiclesCount }})</h3>
        <button class="project-info-btn project-info-btn-small" @click="handleVehiclesEdit">
          Edit-Vehicles
        </button>
      </div>
      <div class="project-info-grid-cards">
        <div 
          v-for="vehicle in vehicles" 
          :key="vehicle.vehicle_id"
          class="project-info-card-item project-info-clickable"
          @click="openVehicleCard(vehicle)"
        >
          <div class="project-info-card-content">
            <span class="project-info-card-name">{{ vehicle.vehicle }}</span>
            <span class="project-info-card-detail">{{ vehicle.type }}</span>
          </div>
        </div>
        <div v-if="!vehicles || vehicles.length === 0" class="project-info-empty">
          No vehicles assigned
        </div>
      </div>
    </div>

    <!-- Interactive Calendar Section -->
    <div class="project-info-section project-info-compact" v-if="project.date_start && project.duration">
      <div class="project-info-section-header">
        <h3>Project Timeline</h3>
        <span class="project-info-calendar-month">{{ currentMonthYear }}</span>
      </div>
      <div class="project-info-calendar">
        <div class="project-info-calendar-header">
          <button class="project-info-btn project-info-btn-calendar" @click="previousMonth">←</button>
          <span class="project-info-calendar-title">{{ currentMonthYear }}</span>
          <button class="project-info-btn project-info-btn-calendar" @click="nextMonth">→</button>
        </div>
        <div class="project-info-calendar-grid">
          <div class="project-info-calendar-weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
          <div 
            v-for="day in calendarDays" 
            :key="day.date"
            :class="['project-info-calendar-day', day.type, { 
              'project-info-calendar-project': day.isProjectDay,
              'project-info-calendar-selected': day.isSelected,
              'project-info-calendar-today': day.isToday
            }]"
            @click="selectDate(day)"
            :title="day.tooltip"
          >
            {{ day.day }}
          </div>
        </div>
        <div class="project-info-calendar-legend">
          <div class="project-info-legend-item">
            <div class="project-info-legend-color project-info-legend-project"></div>
            <span>Project Day</span>
          </div>
          <div class="project-info-legend-item">
            <div class="project-info-legend-color project-info-legend-weekend"></div>
            <span>Weekend</span>
          </div>
          <div class="project-info-legend-item">
            <div class="project-info-legend-color project-info-legend-today"></div>
            <span>Today</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Project Edit Modal -->
    <div v-if="showProjectEdit" class="project-info-modal-overlay" @click="closeProjectEdit">
      <div class="project-info-modal-content project-info-large-modal" @click.stop>
        <button class="project-info-modal-close" @click="closeProjectEdit">×</button>
        <ProjectCard 
          :project="project"
          @close="closeProjectEdit"
          @updated="handleProjectUpdated"
        />
      </div>
    </div>
  </div>
</template>

<script>
import ProjectCard from "./ProjectCard.vue";

export default {
  name: 'ProjectInfo',
  components: {
    ProjectCard
  },
  props: {
    project: {
      type: Object,
      required: true
    },
    personnel: {
      type: Array,
      default: () => []
    },
    vehicles: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      showProjectEdit: false,
      mapLoaded: false,
      currentMonth: new Date().getMonth(),
      currentYear: new Date().getFullYear(),
      selectedDate: null,
      personelProjects: [] // Initialize the data property
    };
  },
  computed: {
    assignedPersonnelCount() {
      return this.personnel ? this.personnel.length : 0;
    },
    
    assignedVehiclesCount() {
      return this.vehicles ? this.vehicles.length : 0;
    },
    
    personnelStatus() {
      const expected = this.project.expected_personel || 0;
      const assigned = this.assignedPersonnelCount;
      return assigned >= expected ? 'project-info-status-good' : 'project-info-status-warning';
    },
    
    mapsLink() {
      if (!this.project.xy_map) return '#';
      const coords = this.project.xy_map.split(',').map(coord => coord.trim());
      if (coords.length === 2) {
        return `https://www.google.com/maps?q=${coords[0]},${coords[1]}`;
      }
      return `https://www.google.com/maps?q=${this.project.xy_map}`;
    },
    
    formattedDateRange() {
      if (!this.project.date_start || !this.project.duration) return 'Dates not set';
      
      const start = new Date(this.project.date_start);
      const end = new Date(start);
      
      // Calculate end date considering only working days
      let workingDays = 1; // Start day counts as 1
      let currentDate = new Date(start);
      
      while (workingDays < this.project.duration) {
        currentDate.setDate(currentDate.getDate() + 1);
        if (this.isWorkingDay(currentDate)) {
          workingDays++;
        }
      }
      end.setTime(currentDate.getTime());
      
      return `${this.formatDate(start)} - ${this.formatDate(end)}`;
    },
    
    weekdays() {
      return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    },
    
    currentMonthYear() {
      return new Date(this.currentYear, this.currentMonth).toLocaleDateString('en-US', {
        month: 'long',
        year: 'numeric'
      });
    },
    
    calendarDays() {
      const days = [];
      const firstDay = new Date(this.currentYear, this.currentMonth, 1);
      const lastDay = new Date(this.currentYear, this.currentMonth + 1, 0);
      
      // Add empty days for padding at start of month
      for (let i = 0; i < firstDay.getDay(); i++) {
        days.push({ date: null, day: '', type: 'project-info-calendar-empty', isProjectDay: false });
      }
      
      // Add actual days of the month
      for (let day = 1; day <= lastDay.getDate(); day++) {
        const date = new Date(this.currentYear, this.currentMonth, day);
        const isProjectDay = this.isProjectDay(date);
        const isToday = this.isToday(date);
        const isWeekend = date.getDay() === 0 || date.getDay() === 6;
        
        days.push({
          date: date.toISOString(),
          day: day,
          type: isWeekend ? 'project-info-calendar-weekend' : 'project-info-calendar-day',
          isProjectDay: isProjectDay,
          isToday: isToday,
          isSelected: this.selectedDate && this.isSameDay(date, new Date(this.selectedDate)),
          tooltip: this.getDayTooltip(date, isProjectDay)
        });
      }
      
      return days;
    },
    
    projectWorkingDays() {
      if (!this.project.date_start || !this.project.duration) return [];
      
      const workingDays = [];
      const startDate = new Date(this.project.date_start);
      let daysAdded = 0;
      let currentDate = new Date(startDate);
      
      while (daysAdded < this.project.duration) {
        if (this.isWorkingDay(currentDate)) {
          workingDays.push(new Date(currentDate));
          daysAdded++;
        }
        currentDate.setDate(currentDate.getDate() + 1);
      }
      
      return workingDays;
    }
  },
  watch: {
    personnel: {
      immediate: true,
      deep: true,
      handler(newVal) {
        // Add null check and handle the personnel data properly
        if (newVal && Array.isArray(newVal)) {
          // If personnel is an array with projects property on items
          if (newVal.length > 0 && newVal[0].projects) {
            this.personelProjects = newVal[0].projects;
          }
        } else if (newVal && newVal.projects) {
          // If personnel is an object with projects property
          this.personelProjects = newVal.projects;
        } else {
          // Initialize empty array if no projects data
          this.personelProjects = [];
        }
      }
    }
  },
  methods: {
    // Add the missing method
    loadPersonelProjects() {
      // This method can be implemented to load personnel projects if needed
      // For now, we'll just ensure it exists to prevent the error
      console.log('loadPersonelProjects called - implement if needed');
      this.personelProjects = [];
    },
    
    formatDate(date) {
      return date.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric',
        year: 'numeric'
      });
    },
    
    isWorkingDay(date) {
      // Monday to Friday are working days (1 = Monday, 5 = Friday)
      const day = date.getDay();
      return day >= 1 && day <= 5;
    },
    
    isProjectDay(date) {
      return this.projectWorkingDays.some(projectDate => 
        this.isSameDay(date, projectDate)
      );
    },
    
    isToday(date) {
      const today = new Date();
      return this.isSameDay(date, today);
    },
    
    isSameDay(date1, date2) {
      return date1.getDate() === date2.getDate() &&
             date1.getMonth() === date2.getMonth() &&
             date1.getFullYear() === date2.getFullYear();
    },
    
    getDayTooltip(date, isProjectDay) {
      const dateStr = date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
      
      if (isProjectDay) {
        return `Project Day: ${dateStr}`;
      }
      
      return dateStr;
    },
    
    selectDate(day) {
      if (!day.date || !this.isWorkingDay(new Date(day.date))) return;
      
      this.selectedDate = day.date;
      // Emit event to parent to change project start date
      this.$emit('change-start-date', day.date);
    },
    
    previousMonth() {
      if (this.currentMonth === 0) {
        this.currentMonth = 11;
        this.currentYear--;
      } else {
        this.currentMonth--;
      }
    },
    
    nextMonth() {
      if (this.currentMonth === 11) {
        this.currentMonth = 0;
        this.currentYear++;
      } else {
        this.currentMonth++;
      }
    },
    
    loadMap() {
      this.mapLoaded = true;
      console.log('Loading map for coordinates:', this.project.xy_map);
    },
    
    handleClose() {
      this.$emit('close');
    },
    
    openMaps() {
      window.open(this.mapsLink, '_blank');
    },
    
    handleProjectEdit() {
      this.showProjectEdit = true;
      // Prevent body scroll when modal is open
      document.body.style.overflow = 'hidden';
    },
    
    closeProjectEdit() {
      this.showProjectEdit = false;
      // Re-enable body scroll
      document.body.style.overflow = '';
    },
    
    handleProjectUpdated() {
      this.closeProjectEdit();
      this.$emit('project-updated');
    },
    
    handlePersonnelEdit() {
      this.$emit('edit-personnel', this.project);
    },
    
    handleVehiclesEdit() {
      this.$emit('edit-vehicles', this.project);
    },
    
    openPersonnelCard(person) {
      this.$emit('open-personnel-card', person, this.project);
    },
    
    openVehicleCard(vehicle) {
      this.$emit('open-vehicle-card', vehicle, this.project);
    }
  },
  mounted() {
    if (this.project.date_start) {
      const startDate = new Date(this.project.date_start);
      this.currentMonth = startDate.getMonth();
      this.currentYear = startDate.getFullYear();
    }
  },
  beforeUnmount() {
    // Cleanup - ensure body scroll is re-enabled when component is destroyed
    document.body.style.overflow = '';
  }
}
</script>

<style scoped>
/* Base Card Styles */
.project-info-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e1e5e9;
  overflow: hidden;
  max-width: 480px;
  margin: 0 auto;
  font-size: 0.9rem;
  position: relative;
  animation: projectInfoEnter 0.3s ease;
}


@keyframes projectInfoEnter {
  from { 
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Header */
.project-info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
}

.project-info-title {
  flex: 1;
}

.project-info-code {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 4px 0;
  opacity: 0.9;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.project-info-name {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.project-info-daterange {
  font-size: 0.85rem;
  opacity: 0.9;
  font-weight: 500;
}

.project-info-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.project-info-btn {
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 600;
}

.project-info-btn-close {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-info-btn-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.project-info-btn-edit {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 0.8rem;
  white-space: nowrap;
}

.project-info-btn-edit:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Sections */
.project-info-section {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f3f4;
}

.project-info-section:last-child {
  border-bottom: none;
}

.project-info-compact {
  padding: 12px 20px;
}

.project-info-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.project-info-section-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1rem;
  font-weight: 600;
}

.project-info-btn-small {
  background: #667eea;
  color: white;
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 0.75rem;
}

.project-info-btn-small:hover {
  background: #5a67d8;
}

/* Info Grid */
.project-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
}

.project-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.project-info-item label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.project-info-location {
  background: #edf2f7;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 500;
  color: #4a5568;
  display: inline-block;
}

.project-info-duration {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.project-info-duration-value {
  font-size: 1rem;
  font-weight: 700;
  color: #2d3748;
}

.project-info-duration-unit {
  font-size: 0.75rem;
  color: #718096;
}

.project-info-crane {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.8rem;
}

.project-info-crane-yes {
  background: #c6f6d5;
  color: #276749;
}

.project-info-crane-no {
  background: #fed7d7;
  color: #c53030;
}

.project-info-personnel-count,
.project-info-vehicles-count {
  font-size: 1rem;
  font-weight: 700;
  padding: 4px 8px;
  border-radius: 6px;
  text-align: center;
  display: inline-block;
}

.project-info-status-good {
  background: #c6f6d5;
  color: #276749;
}

.project-info-status-warning {
  background: #fed7d7;
  color: #c53030;
}

.project-info-vehicles-count {
  background: #e3f2fd;
  color: #1e40af;
}

/* Map */
.project-info-map-link {
  color: #667eea;
  text-decoration: none;
  font-size: 0.8rem;
  font-weight: 500;
}

.project-info-map-link:hover {
  text-decoration: underline;
}

.project-info-map {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  height: 120px;
  overflow: hidden;
  position: relative;
}

.project-info-map-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #718096;
  height: 100%;
}

.project-info-map-icon {
  font-size: 1.5rem;
}

.project-info-map-coords {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.8rem;
}

.project-info-btn-map {
  background: #667eea;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 0.8rem;
}

.project-info-btn-map:hover {
  background: #5a67d8;
}

/* Grid Cards for Personnel and Vehicles */
.project-info-grid-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.project-info-card-item {
  background: #fafafa;
  border: 1px solid #f1f3f4;
  border-radius: 6px;
  padding: 10px;
  transition: all 0.2s ease;
  min-height: 60px;
  display: flex;
  align-items: center;
}

.project-info-clickable {
  cursor: pointer;
}

.project-info-card-item:hover {
  background: #e3f2fd;
  border-color: #90caf9;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.project-info-card-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 100%;
}

.project-info-card-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.85rem;
  line-height: 1.2;
}

.project-info-card-detail {
  font-size: 0.75rem;
  color: #718096;
  line-height: 1.2;
}

.project-info-empty {
  text-align: center;
  padding: 16px;
  color: #a0aec0;
  font-style: italic;
  background: #f7fafc;
  border-radius: 4px;
  border: 1px dashed #e2e8f0;
  font-size: 0.8rem;
  grid-column: 1 / -1;
}

/* Interactive Calendar */
.project-info-calendar-month {
  font-size: 0.8rem;
  color: #718096;
  font-weight: 500;
}

.project-info-calendar {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.project-info-calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 8px;
}

.project-info-btn-calendar {
  background: #667eea;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 3px;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-info-btn-calendar:hover {
  background: #5a67d8;
}

.project-info-calendar-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2d3748;
}

.project-info-calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.project-info-calendar-weekday {
  font-size: 0.7rem;
  font-weight: 600;
  color: #718096;
  text-align: center;
  padding: 4px 0;
  text-transform: uppercase;
}

.project-info-calendar-day {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.project-info-calendar-day.project-info-calendar-day {
  background: #f7fafc;
  color: #2d3748;
}

.project-info-calendar-day.project-info-calendar-weekend {
  background: #e2e8f0;
  color: #718096;
}

.project-info-calendar-day.project-info-calendar-empty {
  background: transparent;
  cursor: default;
}

.project-info-calendar-day.project-info-calendar-project {
  background: #48bb78 !important;
  color: white !important;
  font-weight: 600;
}

.project-info-calendar-day.project-info-calendar-selected {
  border-color: #667eea;
  background: #667eea !important;
  color: white !important;
  transform: scale(1.1);
}

.project-info-calendar-day.project-info-calendar-today {
  border: 2px solid #667eea;
}

.project-info-calendar-day:hover:not(.project-info-calendar-empty) {
  border-color: #667eea;
  transform: scale(1.05);
}

.project-info-calendar-legend {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.project-info-legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  color: #718096;
}

.project-info-legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.project-info-legend-project {
  background: #48bb78;
}

.project-info-legend-weekend {
  background: #e2e8f0;
}

.project-info-legend-today {
  border: 2px solid #667eea;
  background: transparent;
  width: 10px;
  height: 10px;
}

/* MODAL STYLES - Fixed and Improved */
.project-info-modal-overlay {
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

.project-info-modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: auto;
  position: relative;
}

.project-info-large-modal {
  width: 800px;
  max-width: 90vw;
}

/* Ensure ProjectCard fills the modal properly */
.project-info-modal-content ::v-deep .project-card {
  width: 100% !important;
  max-width: none !important;
  margin: 0 !important;
  border: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

/* Modal close button */
.project-info-modal-close {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.1);
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 1.2rem;
  cursor: pointer;
  z-index: 10001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-info-modal-close:hover {
  background: rgba(0, 0, 0, 0.2);
}

/* Responsive */
@media (max-width: 768px) {
  .project-info-card {
    max-width: 100%;
    margin: 0 8px;
  }
  
  .project-info-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .project-info-actions {
    flex-direction: row;
    width: 100%;
    justify-content: space-between;
  }
  
  .project-info-grid {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
  
  .project-info-grid-cards {
    grid-template-columns: 1fr;
  }
  
  .project-info-modal-content {
    width: 95%;
    padding: 10px;
  }
}

@media (max-width: 480px) {
  .project-info-grid {
    grid-template-columns: 1fr;
  }
  
  .project-info-calendar-grid {
    gap: 1px;
  }
  
  .project-info-calendar-day {
    width: 24px;
    height: 24px;
    font-size: 0.7rem;
  }
  
  .project-info-modal-content {
    width: 100%;
    height: 100%;
    max-height: 100%;
    border-radius: 0;
  }

  .project-edit-modal {
  position: fixed !important;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 4000;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.3);
  padding: 1rem;
}
}
</style>
