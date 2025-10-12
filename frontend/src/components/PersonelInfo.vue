<template>
  <div class="personel-info-card">
    <!-- Header Section -->
    <div class="personel-info-header">
      <div class="personel-info-title">
        <h2 class="personel-info-id">{{ personnel.personel_id }}</h2>
        <h1 class="personel-info-name">{{ fullName }}</h1>
        <div class="personel-info-role">
          {{ personnel.role || 'No role specified' }}
        </div>
      </div>
      <div class="personel-info-actions">
        <button class="personel-info-btn personel-info-btn-close" @click="handleClose">
          ×
        </button>
        <button class="personel-info-btn personel-info-btn-edit" @click="handlePersonelEdit">
          EDIT PERSONEL
        </button>
      </div>
    </div>

    <!-- Basic Info Section -->
    <div class="personel-info-section personel-info-compact">
      <div class="personel-info-grid">
        <div class="personel-info-item">
          <label>Role</label>
          <span :class="['personel-info-role-badge', roleClass]">
            {{ personnel.role || 'Not specified' }}
          </span>
        </div>
        <div class="personel-info-item">
          <label>Status</label>
          <span :class="['personel-info-status', statusClass]">
            {{ validityStatus }}
          </span>
        </div>
        <div class="personel-info-item">
          <label>Assigned Projects</label>
          <div class="personel-info-projects-count">
            <span class="personel-info-count-value">{{ assignedProjectsCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Expiration Dates Section -->
    <div class="personel-info-section personel-info-compact">
      <div class="personel-info-section-header">
        <h3>Expiration Dates</h3>
        <div class="personel-info-doc-actions">
          <button class="personel-info-btn personel-info-btn-small" @click="showNewMedicalForm = true">
            + Medical
          </button>
          <button class="personel-info-btn personel-info-btn-small" @click="showNewXrayForm = true">
            + X-Ray
          </button>
          <button class="personel-info-btn personel-info-btn-small" @click="showNewEducationForm = true">
            + Education
          </button>
        </div>
      </div>
      <div class="personel-info-expiration-grid">
        <div class="personel-info-expiration-item" :class="medicalStatusClass">
          <div class="personel-info-expiration-icon">🏥</div>
          <div class="personel-info-expiration-content">
            <span class="personel-info-expiration-label">Medical Exam</span>
            <span class="personel-info-expiration-date">{{ formattedMedicalExpiry }}</span>
            <span class="personel-info-expiration-days">{{ medicalDaysRemaining }}</span>
          </div>
        </div>
        <div class="personel-info-expiration-item" :class="xrayStatusClass">
          <div class="personel-info-expiration-icon">📷</div>
          <div class="personel-info-expiration-content">
            <span class="personel-info-expiration-label">X-Ray</span>
            <span class="personel-info-expiration-date">{{ formattedXrayExpiry }}</span>
            <span class="personel-info-expiration-days">{{ xrayDaysRemaining }}</span>
          </div>
        </div>
        <div class="personel-info-expiration-item" :class="educationStatusClass">
          <div class="personel-info-expiration-icon">🎓</div>
          <div class="personel-info-expiration-content">
            <span class="personel-info-expiration-label">Education</span>
            <span class="personel-info-expiration-date">{{ formattedEducationExpiry }}</span>
            <span class="personel-info-expiration-days">{{ educationDaysRemaining }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Assigned Projects Section -->
    <div class="personel-info-section personel-info-compact">
      <div class="personel-info-section-header">
        <h3>Assigned Projects ({{ assignedProjectsCount }})</h3>
      </div>
      <div class="personel-info-grid-cards">
        <div 
          v-for="project in personelProjects" 
          :key="project.project_id"
          class="personel-info-card-item personel-info-clickable" 
          @click="openProjectCard(project)"
        >
          <div class="personel-info-card-content">
            <span class="personel-info-card-name">{{ project.project_code }}</span>
            <span class="personel-info-card-detail">{{ project.project_name }}</span>
            <span class="personel-info-card-date">{{ formatProjectDate(project.date_start) }}</span>
          </div>
        </div>
        <div v-if="personelProjects.length === 0" class="personel-info-empty">
          No projects assigned
        </div>
      </div>
    </div>

    <!-- Medical Records Section -->
    <div class="personel-info-section personel-info-compact" v-if="personnel.medicals && personnel.medicals.length > 0">
      <div class="personel-info-section-header">
        <h3>Medical Exams ({{ personnel.medicals.length }})</h3>
      </div>
      <div class="personel-info-records-list">
        <div 
          v-for="medical in sortedMedicals" 
          :key="medical.exams_id"
          class="personel-info-record-item"
        >
          <div class="personel-info-record-date">{{ formatDate(medical.exam_date) }}</div>
          <button 
            class="personel-info-btn personel-info-btn-remove"
            @click="removeMedical(medical)"
            title="Remove medical record"
          >
            ×
          </button>
        </div>
      </div>
    </div>

    <!-- X-Ray Records Section -->
    <div class="personel-info-section personel-info-compact" v-if="personnel.xrays && personnel.xrays.length > 0">
      <div class="personel-info-section-header">
        <h3>X-Ray Exams ({{ personnel.xrays.length }})</h3>
      </div>
      <div class="personel-info-records-list">
        <div 
          v-for="xray in sortedXrays" 
          :key="xray.xray_id"
          class="personel-info-record-item"
        >
          <div class="personel-info-record-date">{{ formatDate(xray.xrays_date) }}</div>
          <button 
            class="personel-info-btn personel-info-btn-remove"
            @click="removeXray(xray)"
            title="Remove X-Ray record"
          >
            ×
          </button>
        </div>
      </div>
    </div>

    <!-- Education Records Section -->
    <div class="personel-info-section personel-info-compact" v-if="personnel.education && personnel.education.length > 0">
      <div class="personel-info-section-header">
        <h3>Education Records ({{ personnel.education.length }})</h3>
      </div>
      <div class="personel-info-records-list">
        <div 
          v-for="education in sortedEducation" 
          :key="education.education_id"
          class="personel-info-record-item"
        >
          <div class="personel-info-record-date">{{ formatDate(education.education_date) }}</div>
          <span class="personel-info-record-badge" v-if="education.first_time === 'Yes'">First Time</span>
          <button 
            class="personel-info-btn personel-info-btn-remove"
            @click="removeEducation(education)"
            title="Remove education record"
          >
            ×
          </button>
        </div>
      </div>
    </div>

    <!-- Assignment Calendar Section -->
    <div class="personel-info-section personel-info-compact">
      <div class="personel-info-section-header">
        <h3>4-Week Assignment Overview</h3>
        <span class="personel-info-calendar-range">{{ calendarDateRange }}</span>
      </div>
      <div class="personel-info-calendar">
        <div class="personel-info-calendar-grid">
          <div class="personel-info-calendar-weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
          <div 
            v-for="day in calendarDays" 
            :key="day.date"
            :class="['personel-info-calendar-day', day.assignmentClass, {
              'personel-info-calendar-today': day.isToday
            }]"
            :title="day.tooltip"
          >
            {{ day.day }}
          </div>
        </div>
        <div class="personel-info-calendar-legend">
          <div class="personel-info-legend-item">
            <div class="personel-info-legend-color personel-info-legend-free"></div>
            <span>Free</span>
          </div>
          <div class="personel-info-legend-item">
            <div class="personel-info-legend-color personel-info-legend-busy"></div>
            <span>1 Project</span>
          </div>
          <div class="personel-info-legend-item">
            <div class="personel-info-legend-color personel-info-legend-busy2"></div>
            <span>2-3 Projects</span>
          </div>
          <div class="personel-info-legend-item">
            <div class="personel-info-legend-color personel-info-legend-overloaded"></div>
            <span>4+ Projects</span>
          </div>
        </div>
      </div>
    </div>

    <!-- New Medical Form Modal -->
    <div v-if="showNewMedicalForm" class="personel-info-modal-overlay" @click="showNewMedicalForm = false">
      <div class="personel-info-modal-content" @click.stop>
        <button class="personel-info-modal-close" @click="showNewMedicalForm = false">×</button>
        <h3>Add Medical Exam</h3>
        <form @submit.prevent="createMedical">
          <div class="personel-info-form-group">
            <label>Exam Date:</label>
            <input type="date" v-model="newMedical.exam_date" required>
          </div>
          <div class="personel-info-form-actions">
            <button type="button" @click="showNewMedicalForm = false">Cancel</button>
            <button type="submit" :disabled="creatingMedical">
              {{ creatingMedical ? 'Adding...' : 'Add Medical Exam' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- New X-Ray Form Modal -->
    <div v-if="showNewXrayForm" class="personel-info-modal-overlay" @click="showNewXrayForm = false">
      <div class="personel-info-modal-content" @click.stop>
        <button class="personel-info-modal-close" @click="showNewXrayForm = false">×</button>
        <h3>Add X-Ray Exam</h3>
        <form @submit.prevent="createXray">
          <div class="personel-info-form-group">
            <label>X-Ray Date:</label>
            <input type="date" v-model="newXray.xrays_date" required>
          </div>
          <div class="personel-info-form-actions">
            <button type="button" @click="showNewXrayForm = false">Cancel</button>
            <button type="submit" :disabled="creatingXray">
              {{ creatingXray ? 'Adding...' : 'Add X-Ray Exam' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- New Education Form Modal -->
    <div v-if="showNewEducationForm" class="personel-info-modal-overlay" @click="showNewEducationForm = false">
      <div class="personel-info-modal-content" @click.stop>
        <button class="personel-info-modal-close" @click="showNewEducationForm = false">×</button>
        <h3>Add Education Record</h3>
        <form @submit.prevent="createEducation">
          <div class="personel-info-form-group">
            <label>Education Date:</label>
            <input type="date" v-model="newEducation.education_date" required>
          </div>
          <div class="personel-info-form-group">
            <label>First Time:</label>
            <select v-model="newEducation.first_time">
              <option value="Yes">Yes</option>
              <option value="No">No</option>
            </select>
          </div>
          <div class="personel-info-form-actions">
            <button type="button" @click="showNewEducationForm = false">Cancel</button>
            <button type="submit" :disabled="creatingEducation">
              {{ creatingEducation ? 'Adding...' : 'Add Education Record' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Personel Edit Modal -->
    <div v-if="showPersonelEdit" class="personel-info-modal-overlay" @click="closePersonelEdit">
      <div class="personel-info-modal-content" @click.stop>
        <button class="personel-info-modal-close" @click="closePersonelEdit">×</button>
        <h3>Edit Personel</h3>
        <form @submit.prevent="updatePersonel">
          <div class="personel-info-form-group">
            <label>First Name:</label>
            <input type="text" v-model="editPersonelData.personel_name" required>
          </div>
          <div class="personel-info-form-group">
            <label>Last Name:</label>
            <input type="text" v-model="editPersonelData.personel_surname" required>
          </div>
          <div class="personel-info-form-group">
            <label>Role:</label>
            <select v-model="editPersonelData.role" required>
              <option value="Engineer">Engineer</option>
              <option value="Worker">Worker</option>
              <option value="Driver">Driver</option>
            </select>
          </div>
          <div class="personel-info-form-actions">
            <button type="button" @click="closePersonelEdit">Cancel</button>
            <button type="submit" :disabled="updating">
              {{ updating ? 'Updating...' : 'Update Personel' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: 'PersonelInfo',
  props: {
    personnel: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      showPersonelEdit: false,
      showNewMedicalForm: false,
      showNewXrayForm: false,
      showNewEducationForm: false,
      creatingMedical: false,
      creatingXray: false,
      creatingEducation: false,
      updating: false,
      newMedical: {
        exam_date: '',
        personel_id: null
      },
      newXray: {
        xrays_date: '',
        personel_id: null
      },
      newEducation: {
        education_date: '',
        first_time: 'Yes',
        personel_id: null
      },
      editPersonelData: {
        personel_name: '',
        personel_surname: '',
        role: ''
      },
      personelProjects: []
    };
  },
  computed: {
    fullName() {
      return `${this.personnel.personel_name || ''} ${this.personnel.personel_surname || ''}`.trim();
    },
    
    assignedProjectsCount() {
      return this.personelProjects.length;
    },
    
    roleClass() {
      const roleMap = {
        'Engineer': 'personel-info-role-engineer',
        'Worker': 'personel-info-role-worker',
        'Driver': 'personel-info-role-driver'
      };
      return roleMap[this.personnel.role] || 'personel-info-role-default';
    },
    
    validityStatus() {
      const medicalExpiry = this.medicalExpirationDate;
      const educationExpiry = this.educationExpirationDate;
      const xrayExpiry = this.xrayExpirationDate;

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
    
    statusClass() {
      const statusMap = {
        'VALID': 'personel-info-status-valid',
        'ALMOST': 'personel-info-status-almost',
        'INVALID': 'personel-info-status-invalid'
      };
      return statusMap[this.validityStatus] || 'personel-info-status-invalid';
    },
    
    // Medical expiration calculations
    medicalExpirationDate() {
      if (!this.personnel.medicals || this.personnel.medicals.length === 0) return null;
      const latestMedical = this.sortedMedicals[0];
      const examDate = new Date(latestMedical.exam_date);
      examDate.setFullYear(examDate.getFullYear() + 1);
      return examDate.toISOString().split('T')[0];
    },
    
    formattedMedicalExpiry() {
      return this.formatDate(this.medicalExpirationDate);
    },
    
    medicalDaysRemaining() {
      return this.getDaysRemaining(this.medicalExpirationDate);
    },
    
    medicalStatusClass() {
      return `personel-info-expiration-${this.getDateStatus(this.medicalExpirationDate)}`;
    },
    
    // X-Ray expiration calculations
    xrayExpirationDate() {
      if (!this.personnel.xrays || this.personnel.xrays.length === 0) return null;
      const latestXray = this.sortedXrays[0];
      const xrayDate = new Date(latestXray.xrays_date);
      xrayDate.setFullYear(xrayDate.getFullYear() + 2);
      return xrayDate.toISOString().split('T')[0];
    },
    
    formattedXrayExpiry() {
      return this.formatDate(this.xrayExpirationDate);
    },
    
    xrayDaysRemaining() {
      return this.getDaysRemaining(this.xrayExpirationDate);
    },
    
    xrayStatusClass() {
      return `personel-info-expiration-${this.getDateStatus(this.xrayExpirationDate)}`;
    },
    
    // Education expiration calculations
    educationExpirationDate() {
      if (!this.personnel.education || this.personnel.education.length === 0) return null;
      const latestEducation = this.sortedEducation[0];
      const educationDate = new Date(latestEducation.education_date);
      educationDate.setFullYear(educationDate.getFullYear() + 3);
      return educationDate.toISOString().split('T')[0];
    },
    
    formattedEducationExpiry() {
      return this.formatDate(this.educationExpirationDate);
    },
    
    educationDaysRemaining() {
      return this.getDaysRemaining(this.educationExpirationDate);
    },
    
    educationStatusClass() {
      return `personel-info-expiration-${this.getDateStatus(this.educationExpirationDate)}`;
    },
    
    // Sorted records (newest first)
    sortedMedicals() {
      if (!this.personnel.medicals) return [];
      return [...this.personnel.medicals].sort((a, b) => 
        new Date(b.exam_date) - new Date(a.exam_date)
      );
    },
    
    sortedXrays() {
      if (!this.personnel.xrays) return [];
      return [...this.personnel.xrays].sort((a, b) => 
        new Date(b.xrays_date) - new Date(a.xrays_date)
      );
    },
    
    sortedEducation() {
      if (!this.personnel.education) return [];
      return [...this.personnel.education].sort((a, b) => 
        new Date(b.education_date) - new Date(a.education_date)
      );
    },
    
    // Calendar properties
    weekdays() {
      return ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    },
    
    calendarDateRange() {
      if (this.calendarDays.length === 0) return '';
      const start = new Date(this.calendarDays[0].date);
      const end = new Date(this.calendarDays[this.calendarDays.length - 1].date);
      return `${this.formatDate(start)} - ${this.formatDate(end)}`;
    },
    
    calendarDays() {
      const days = [];
      const today = new Date();
      
      // Find the Monday of the current week
      const startDate = new Date(today);
      const dayOfWeek = startDate.getDay();
      const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
      startDate.setDate(today.getDate() + diffToMonday);
      
      // Create 28 days (4 weeks) starting from Monday
      for (let i = 0; i < 28; i++) {
        const date = new Date(startDate);
        date.setDate(startDate.getDate() + i);
        
        const isWeekend = date.getDay() === 0 || date.getDay() === 6;
        let assignmentClass = 'personel-info-calendar-free';
        let tooltip = '';
        
        if (isWeekend) {
          tooltip = `${this.formatDate(date)} - Weekend (No assignments)`;
        } else {
          const assignments = this.getAssignmentsForDate(date);
          const assignmentCount = assignments.length;
          
          if (assignmentCount === 1) {
            assignmentClass = 'personel-info-calendar-busy';
          } else if (assignmentCount >= 2 && assignmentCount <= 3) {
            assignmentClass = 'personel-info-calendar-busy2';
          } else if (assignmentCount >= 4) {
            assignmentClass = 'personel-info-calendar-overloaded';
          }
          
          tooltip = this.getDayTooltip(date, assignments);
        }
        
        days.push({
          date: date.toISOString(),
          day: date.getDate(),
          dayOfWeek: date.getDay(),
          assignmentClass: assignmentClass,
          isToday: this.isToday(date),
          tooltip: tooltip,
          isWeekend: isWeekend
        });
      }
      
      return days;
    }
  },
  methods: {
    async loadPersonelProjects() {
      try {
        const response = await axios.get(`http://localhost:8000/personel/${this.personnel.personel_id}/projects`);
        this.personelProjects = response.data;
      } catch (error) {
        console.error("Failed to load personel projects", error);
        this.personelProjects = [];
      }
    },

    // Date utilities
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },
    
    formatProjectDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric'
      });
    },
    
    getDaysRemaining(dateString) {
      if (!dateString) return 'N/A';
      const today = new Date();
      const expiryDate = new Date(dateString);
      const diffTime = expiryDate - today;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays > 0 ? `${diffDays}d` : 'Expired';
    },
    
    getDateStatus(dateString) {
      if (!dateString) return 'no-date';
      const today = new Date();
      const expiryDate = new Date(dateString);
      const diffTime = expiryDate - today;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays < 0) return 'expired';
      if (diffDays <= 60) return 'warning';
      return 'valid';
    },
    
    isToday(date) {
      const today = new Date();
      return date.toDateString() === today.toDateString();
    },
    
    isWeekend(date) {
      const day = date.getDay();
      return day === 0 || day === 6;
    },
    
    calculateProjectEndDate(project) {
      if (!project.date_start || !project.duration) return new Date(project.date_start);
      
      const startDate = new Date(project.date_start);
      const workingDays = parseInt(project.duration) || 0;
      let currentDate = new Date(startDate);
      
      if (workingDays <= 1) {
        return new Date(startDate);
      }
      
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
    
    getAssignmentsForDate(date) {
      if (!this.personelProjects || this.personelProjects.length === 0) return [];
      
      return this.personelProjects.filter(project => {
        // if (!project.date_start) return false;
        
        const startDate = new Date(project.date_start);
        const endDate = this.calculateProjectEndDate(project);
        
        return date >= startDate && date <= endDate && !this.isWeekend(date);
      });
    },
    
    getDayTooltip(date, assignments) {
      const dateStr = this.formatDate(date);
      if (assignments.length === 0) {
        return `${dateStr} - No assignments`;
      }
      const projectNames = assignments.map(p => p.project_code).join(', ');
      return `${dateStr} - ${assignments.length} project(s): ${projectNames}`;
    },
    
    // Event handlers
    handleClose() {
      this.$emit('close');
    },
    
    handlePersonelEdit() {
      this.editPersonelData = {
        personel_name: this.personnel.personel_name,
        personel_surname: this.personnel.personel_surname,
        role: this.personnel.role
      };
      this.showPersonelEdit = true;
    },
    
    closePersonelEdit() {
      this.showPersonelEdit = false;
    },
    
    openProjectCard(project) {
      this.$emit('open-project-card', project);
    },
    
    // API methods
    async createMedical() {
      try {
        this.creatingMedical = true;
        this.newMedical.personel_id = this.personnel.personel_id;
        
        const response = await axios.post(
          "http://localhost:8000/medicals/create", 
          this.newMedical
        );

        this.$emit('medical-created', response.data);
        this.showNewMedicalForm = false;
        this.newMedical = { exam_date: '', personel_id: null };
        
      } catch (error) {
        console.error("Failed to create medical record", error);
        alert("Failed to create medical record. Please try again.");
      } finally {
        this.creatingMedical = false;
      }
    },
    
    async createXray() {
      try {
        this.creatingXray = true;
        this.newXray.personel_id = this.personnel.personel_id;
        
        const response = await axios.post(
          "http://localhost:8000/xrays/create", 
          this.newXray
        );

        this.$emit('xray-created', response.data);
        this.showNewXrayForm = false;
        this.newXray = { xrays_date: '', personel_id: null };
        
      } catch (error) {
        console.error("Failed to create X-Ray record", error);
        alert("Failed to create X-Ray record. Please try again.");
      } finally {
        this.creatingXray = false;
      }
    },
    
    async createEducation() {
      try {
        this.creatingEducation = true;
        this.newEducation.personel_id = this.personnel.personel_id;
        
        const response = await axios.post(
          "http://localhost:8000/educations/create", 
          this.newEducation
        );

        this.$emit('education-created', response.data);
        this.showNewEducationForm = false;
        this.newEducation = { education_date: '', first_time: 'Yes', personel_id: null };
        
      } catch (error) {
        console.error("Failed to create education record", error);
        alert("Failed to create education record. Please try again.");
      } finally {
        this.creatingEducation = false;
      }
    },
    
    async removeMedical(medical) {
      if (confirm('Are you sure you want to remove this medical record?')) {
        try {
          await axios.post(`http://localhost:8000/medicals/${medical.exams_id}/remove`);
          this.$emit('medical-removed', medical.exams_id);
        } catch (error) {
          console.error("Failed to remove medical record", error);
          alert('Failed to remove medical record. Please try again.');
        }
      }
    },
    
    async removeXray(xray) {
      if (confirm('Are you sure you want to remove this X-Ray record?')) {
        try {
          await axios.post(`http://localhost:8000/xrays/${xray.xray_id}/remove`);
          this.$emit('xray-removed', xray.xray_id);
        } catch (error) {
          console.error("Failed to remove X-Ray record", error);
          alert('Failed to remove X-Ray record. Please try again.');
        }
      }
    },
    
    async removeEducation(education) {
      if (confirm('Are you sure you want to remove this education record?')) {
        try {
          await axios.post(`http://localhost:8000/educations/${education.education_id}/remove`);
          this.$emit('education-removed', education.education_id);
        } catch (error) {
          console.error("Failed to remove education record", error);
          alert('Failed to remove education record. Please try again.');
        }
      }
    },
    
    async updatePersonel() {
      try {
        this.updating = true;
        
        const response = await axios.patch(
          `http://localhost:8000/personel/${this.personnel.personel_id}/update`,
          this.editPersonelData
        );
        
        this.$emit('personel-updated', response.data);
        this.closePersonelEdit();
        
      } catch (error) {
        console.error("Failed to update personel", error);
        alert('Failed to update personel. Please try again.');
      } finally {
        this.updating = false;
      }
    }
  },
  mounted() {
    const today = new Date().toISOString().split('T')[0];
    this.newMedical.exam_date = today;
    this.newXray.xrays_date = today;
    this.newEducation.education_date = today;
    this.loadPersonelProjects();
  },
  watch: {
    personnel: {
      immediate: true,
      handler() {
        this.loadPersonelProjects();
      }
    }
  }
}
</script>

<style scoped>
/* Base Card Styles */
.personel-info-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e1e5e9;
  overflow: hidden;
  max-width: 480px;
  margin: 0 auto;
  font-size: 0.9rem;
  position: relative;
  animation: personelInfoEnter 0.25s ease-out;
}


@keyframes personelInfoEnter {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Header */
.personel-info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
  position: relative;
}

.personel-info-title {
  flex: 1;
}

.personel-info-id {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 4px 0;
  opacity: 0.9;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.personel-info-name {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.personel-info-role {
  font-size: 0.85rem;
  opacity: 0.9;
  font-weight: 500;
}

.personel-info-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.personel-info-btn {
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 600;
}

.personel-info-btn-close {
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

.personel-info-btn-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.personel-info-btn-edit {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 0.8rem;
  white-space: nowrap;
}

.personel-info-btn-edit:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Sections */
.personel-info-section {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f3f4;
}

.personel-info-section:last-child {
  border-bottom: none;
}

.personel-info-compact {
  padding: 12px 20px;
}

.personel-info-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.personel-info-section-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1rem;
  font-weight: 600;
}

.personel-info-btn-small {
  background: #48bb78;
  color: white;
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 0.75rem;
  margin-left: 4px;
}

.personel-info-btn-small:hover {
  background: #38a169;
}

/* Info Grid */
.personel-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
}

.personel-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.personel-info-item label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Role Badges */
.personel-info-role-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.8rem;
  text-align: center;
}

.personel-info-role-engineer {
  background: #e3f2fd;
  color: #1e40af;
}

.personel-info-role-worker {
  background: #fff3cd;
  color: #856404;
}

.personel-info-role-driver {
  background: #d1ecf1;
  color: #0c5460;
}

.personel-info-role-default {
  background: #f8f9fa;
  color: #6c757d;
}

/* Status Badges */
.personel-info-status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.8rem;
  text-align: center;
}

.personel-info-status-valid {
  background: #c6f6d5;
  color: #276749;
}

.personel-info-status-almost {
  background: #fed7d7;
  color: #c53030;
}

.personel-info-status-invalid {
  background: #fed7d7;
  color: #c53030;
}

/* Projects Count */
.personel-info-projects-count {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.personel-info-count-value {
  font-size: 1rem;
  font-weight: 700;
  color: #2d3748;
  background: #e3f2fd;
  padding: 4px 8px;
  border-radius: 6px;
  text-align: center;
  display: inline-block;
}

/* Expiration Dates Grid */
.personel-info-expiration-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 8px;
}

.personel-info-expiration-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.personel-info-expiration-valid {
  background: #f0fff4;
  border-color: #9ae6b4;
}

.personel-info-expiration-warning {
  background: #fffaf0;
  border-color: #fbd38d;
}

.personel-info-expiration-expired {
  background: #fed7d7;
  border-color: #fc8181;
}

.personel-info-expiration-no-date {
  background: #f7fafc;
  border-color: #e2e8f0;
}

.personel-info-expiration-icon {
  font-size: 1.2rem;
}

.personel-info-expiration-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.personel-info-expiration-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #718096;
  text-transform: uppercase;
}

.personel-info-expiration-date {
  font-size: 0.8rem;
  font-weight: 600;
  color: #2d3748;
}

.personel-info-expiration-days {
  font-size: 0.7rem;
  font-weight: 600;
}

.personel-info-expiration-valid .personel-info-expiration-days {
  color: #38a169;
}

.personel-info-expiration-warning .personel-info-expiration-days {
  color: #dd6b20;
}

.personel-info-expiration-expired .personel-info-expiration-days {
  color: #e53e3e;
}

.personel-info-expiration-no-date .personel-info-expiration-days {
  color: #a0aec0;
}

/* Grid Cards for Projects */
.personel-info-grid-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.personel-info-card-item {
  background: #fafafa;
  border: 1px solid #f1f3f4;
  border-radius: 6px;
  padding: 10px;
  transition: all 0.2s ease;
  min-height: 60px;
  display: flex;
  align-items: center;
}

.personel-info-clickable {
  cursor: pointer;
}

.personel-info-card-item:hover {
  background: #e3f2fd;
  border-color: #90caf9;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.personel-info-card-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 100%;
}

.personel-info-card-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.85rem;
  line-height: 1.2;
}

.personel-info-card-detail {
  font-size: 0.75rem;
  color: #718096;
  line-height: 1.2;
}

.personel-info-card-date {
  font-size: 0.7rem;
  color: #a0aec0;
  margin-top: 2px;
}

.personel-info-empty {
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

/* Records List */
.personel-info-records-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.personel-info-record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f7fafc;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
}

.personel-info-record-date {
  font-size: 0.8rem;
  font-weight: 500;
  color: #4a5568;
}

.personel-info-record-badge {
  background: #e3f2fd;
  color: #1e40af;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.7rem;
  font-weight: 600;
}

.personel-info-btn-remove {
  background: #fed7d7;
  color: #c53030;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.personel-info-btn-remove:hover {
  background: #feb2b2;
}

/* Calendar */
.personel-info-calendar-range {
  font-size: 0.8rem;
  color: #718096;
  font-weight: 500;
}

.personel-info-calendar {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.personel-info-calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.personel-info-calendar-weekday {
  font-size: 0.7rem;
  font-weight: 600;
  color: #718096;
  text-align: center;
  padding: 4px 0;
  text-transform: uppercase;
}

.personel-info-calendar-day {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  position: relative;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.personel-info-calendar-free {
  background: #f7fafc;
  color: #a0aec0;
}

.personel-info-calendar-busy {
  background: #48bb78;
  color: white;
  font-weight: 600;
}

.personel-info-calendar-busy2 {
  background: #ed8936;
  color: white;
  font-weight: 600;
}

.personel-info-calendar-overloaded {
  background: #e53e3e;
  color: white;
  font-weight: 600;
}

.personel-info-calendar-today {
  border: 2px solid #667eea !important;
}

.personel-info-calendar-day:hover {
  transform: scale(1.05);
  border-color: #667eea;
}

.personel-info-calendar-legend {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.personel-info-legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  color: #718096;
}

.personel-info-legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.personel-info-legend-free {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
}

.personel-info-legend-busy {
  background: #48bb78;
}

.personel-info-legend-busy2 {
  background: #ed8936;
}

.personel-info-legend-overloaded {
  background: #e53e3e;
}

/* Personel Info Modal Styles - FIXED SCROLLBAR */
.personel-info-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 20px;
  box-sizing: border-box;
  overflow: hidden;
  /* Prevent any overflow */
}

.personel-info-modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 500px;
  max-width: 95vw;
  max-height: 95vh;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* No scrolling on modal itself */
  padding: 0 !important;
  /* Force remove any padding */
  margin: 0 !important;
  /* Force remove any margin */
}

/* Make the card fill the modal completely */
.personel-info-modal-content .personel-info-card {
  max-width: none !important;
  margin: 0 !important;
  border-radius: 12px !important;
  flex: 1;
  overflow-y: auto;
  /* Only the card content scrolls */
  overflow-x: hidden;
  width: 100%;
  height: 100%;
  box-shadow: none !important;
  border: none !important;
}

/* Remove any inherited styles that might cause double scroll */
.personel-info-modal-content ::v-deep * {
  box-sizing: border-box;
}

/* Ensure the card doesn't have any max-height that would cause double scroll */
.personel-info-card {
  max-height: none !important;
}

/* Media queries for responsive design */
@media (max-width: 768px) {
  .personel-info-modal-overlay {
    padding: 10px;
  }

  .personel-info-modal-content {
    width: 100%;
    max-width: 100%;
    max-height: 100%;
    border-radius: 0;
  }

  .personel-info-modal-content .personel-info-card {
    border-radius: 0 !important;
  }
}

/* Form Styles */
.personel-info-form-group {
  margin-bottom: 16px;
}

.personel-info-form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
}

.personel-info-form-group input,
.personel-info-form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 0.9rem;
  box-sizing: border-box;
}

.personel-info-form-group input:focus,
.personel-info-form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.personel-info-form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.personel-info-form-actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.personel-info-form-actions button[type="button"] {
  background: #e2e8f0;
  color: #4a5568;
}

.personel-info-form-actions button[type="submit"] {
  background: #48bb78;
  color: white;
}

.personel-info-form-actions button[type="submit"]:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.personel-info-form-actions button:hover:not(:disabled) {
  transform: translateY(-1px);
}

.personel-info-form-actions button[type="button"]:hover {
  background: #cbd5e0;
}

.personel-info-form-actions button[type="submit"]:hover:not(:disabled) {
  background: #38a169;
}

/* Responsive */
@media (max-width: 768px) {
  .personel-info-card {
    max-width: 100%;
    margin: 0 8px;
  }

  .personel-info-header {
    flex-direction: column;
    gap: 12px;
  }

  .personel-info-actions {
    flex-direction: row;
    width: 100%;
    justify-content: space-between;
  }

  .personel-info-grid {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }

  .personel-info-expiration-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .personel-info-grid-cards {
    grid-template-columns: 1fr;
  }

  .personel-info-modal-content {
    width: 95%;
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .personel-info-grid {
    grid-template-columns: 1fr;
  }

  .personel-info-calendar-grid {
    gap: 1px;
  }

  .personel-info-calendar-day {
    width: 24px;
    height: 24px;
    font-size: 0.7rem;
  }

  .personel-info-modal-content {
    width: 100%;
    height: 100%;
    max-height: 100%;
    border-radius: 0;
  }
}
</style>