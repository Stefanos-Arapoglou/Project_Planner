<template>
  <div class="personnel-page">
    <!-- Header Section -->
    <div class="dashboard-header">
      <h1>Personnel Management</h1>
    </div>

    <!-- Controls Section -->
    <div class="controls">
      <div class="search-box">
        <input 
          type="text" 
          v-model="filters.search"
          placeholder="Search by name, surname, or role..."
        >
      </div>
      <div class="filter-controls">
        <select v-model="filters.validity">
          <option value="">All Status</option>
          <option value="VALID">Valid</option>
          <option value="ALMOST">Almost Expired</option>
          <option value="INVALID">Invalid</option>
        </select>
        <select v-model="filters.role">
          <option value="">All Roles</option>
          <option v-for="role in roles" :key="role" :value="role">
            {{ role }}
          </option>
        </select>
        <button class="btn btn-create" @click="showCreateModal = true">
          + Create New Personnel
        </button>
      </div>
    </div>

    <!-- Main Table -->
    <div class="table-container">
      <table v-if="filteredPersonnel.length > 0" class="bordered-table excel-style-table">
        <thead>
          <tr>
            <th class="col-name">Name</th>
            <th class="col-surname">Surname</th>
            <th class="col-role">Role</th>
            <th class="col-medical">Medical Expiry</th>
            <th class="col-education">Education Expiry</th>
            <th class="col-xray">X-Ray Expiry</th>
            <th class="col-status">Status</th>
            <th class="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="person in filteredPersonnel" 
            :key="person.personel_id" 
            class="table-row"
          >
            <td class="col-name cell-bordered">
              <strong>{{ person.personel_name }}</strong>
            </td>
            <td class="col-surname cell-bordered">
              <strong>{{ person.personel_surname }}</strong>
            </td>
            <td class="col-role cell-bordered">
              <div :class="['role-cell', getRoleClass(person.role)]">
                <span class="role-text">{{ person.role }}</span>
              </div>
            </td>
            <td class="col-medical cell-bordered">
              <div class="date-cell clickable" :class="getDateStatus(getMedicalExpiration(person))" @click="openMedicalModal(person)">
                <span class="date">{{ formatDate(getMedicalExpiration(person)) }}</span>
                <span class="days-remaining">{{ getDaysRemaining(getMedicalExpiration(person)) }}</span>
              </div>
            </td>
            <td class="col-education cell-bordered">
              <div class="date-cell clickable" :class="getDateStatus(getEducationExpiration(person))" @click="openEducationModal(person)">
                <span class="date">{{ formatDate(getEducationExpiration(person)) }}</span>
                <span class="days-remaining">{{ getDaysRemaining(getEducationExpiration(person)) }}</span>
              </div>
            </td>
            <td class="col-xray cell-bordered">
              <div class="date-cell clickable" :class="getDateStatus(getXrayExpiration(person))" @click="openXrayModal(person)">
                <span class="date">{{ formatDate(getXrayExpiration(person)) }}</span>
                <span class="days-remaining">{{ getDaysRemaining(getXrayExpiration(person)) }}</span>
              </div>
            </td>
            <td class="col-status cell-bordered">
              <div :class="['status-cell', `status-${getValidityStatus(person)}`]">
                <span class="status-text">{{ getValidityStatus(person) }}</span>
              </div>
            </td>
            <td class="col-actions cell-bordered">
              <div class="action-buttons">
                <button class="btn btn-edit" @click="editPersonnel(person)">
                  Edit
                </button>
                <button class="btn btn-view" @click="viewPersonnel(person)">
                  View
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty-state">
        <div class="empty-icon">👥</div>
        <h3>No personnel found</h3>
        <p>Try adjusting your search or filters</p>
      </div>
    </div>

    <!-- Create Personnel Modal -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal-content create-modal" @click.stop>
        <h3>Create New Personnel</h3>
        <form @submit.prevent="createPersonnel" class="create-form">
          <div class="form-row">
            <div class="form-group">
              <label for="personel_name">First Name *</label>
              <input 
                type="text" 
                id="personel_name"
                v-model="newPersonnel.personel_name" 
                required
                placeholder="Enter first name"
              >
            </div>
            <div class="form-group">
              <label for="personel_surname">Last Name *</label>
              <input 
                type="text" 
                id="personel_surname"
                v-model="newPersonnel.personel_surname" 
                required
                placeholder="Enter last name"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="role">Role *</label>
              <select id="role" v-model="newPersonnel.role" required>
                <option value="">Select Role</option>
                <option value="Engineer">Engineer</option>
                <option value="Worker">Worker</option>
                <option value="Driver">Driver</option>
              </select>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModals" class="btn btn-cancel">
              Cancel
            </button>
            <button type="submit" class="btn btn-submit" :disabled="creating">
              {{ creating ? 'Creating...' : 'Create Personnel' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Edit Personnel Modal -->
    <div v-if="showEditModal && selectedPersonnel" class="modal-overlay">
      <div class="modal-content create-modal" @click.stop>
        <h3>Edit Personnel - {{ selectedPersonnel.personel_name }} {{ selectedPersonnel.personel_surname }}</h3>
        <form @submit.prevent="updatePersonnel" class="create-form">
          <div class="form-row">
            <div class="form-group">
              <label for="edit_personel_name">First Name *</label>
              <input 
                type="text" 
                id="edit_personel_name"
                v-model="editPersonnelData.personel_name" 
                required
                placeholder="Enter first name"
              >
            </div>
            <div class="form-group">
              <label for="edit_personel_surname">Last Name *</label>
              <input 
                type="text" 
                id="edit_personel_surname"
                v-model="editPersonnelData.personel_surname" 
                required
                placeholder="Enter last name"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="edit_role">Role *</label>
              <select id="edit_role" v-model="editPersonnelData.role" required>
                <option value="">Select Role</option>
                <option value="Engineer">Engineer</option>
                <option value="Worker">Worker</option>
                <option value="Driver">Driver</option>
              </select>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-danger" @click="deletePersonnel" :disabled="deleting">
              {{ deleting ? 'Deleting...' : 'Delete Personnel' }}
            </button>
            <div class="form-actions-right">
              <button type="button" @click="closeModals" class="btn btn-cancel">
                Cancel
              </button>
              <button type="submit" class="btn btn-submit" :disabled="updating">
                {{ updating ? 'Updating...' : 'Update Personnel' }}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- Medical Records Modal -->
    <div v-if="showMedicalModal && selectedPersonnel" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Medical Records - {{ selectedPersonnel.personel_name }} {{ selectedPersonnel.personel_surname }}</h3>
        <div class="records-list">
          <div v-for="medical in (selectedPersonnel.medicals || [])" :key="medical.exams_id" class="record-item">
            <div class="record-info">
              <strong>Exam Date:</strong> {{ formatDate(medical.exam_date) }}<br>
              <strong>Expiration:</strong> {{ formatDate(medical.exam_expiration_date) }}
            </div>
            <button class="btn btn-remove" @click="removeMedical(medical)">Remove</button>
          </div>
          <div v-if="!selectedPersonnel.medicals || selectedPersonnel.medicals.length === 0" class="no-records">
            No medical records found
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeModals" class="btn btn-close">Close</button>
          <button class="btn btn-add" @click="showNewMedicalForm = true">+ Add New Medical</button>
        </div>
      </div>
    </div>

    <!-- New Medical Form Modal -->
    <div v-if="showNewMedicalForm" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Add New Medical Record</h3>
        <form @submit.prevent="createMedical" class="create-form">
          <div class="form-group">
            <label for="exam_date">Exam Date *</label>
            <input 
              type="date" 
              id="exam_date"
              v-model="newMedical.exam_date" 
              required
            >
          </div>
          <div class="form-actions">
            <button type="button" @click="showNewMedicalForm = false" class="btn btn-cancel">
              Cancel
            </button>
            <button type="submit" class="btn btn-submit" :disabled="creatingMedical">
              {{ creatingMedical ? 'Creating...' : 'Create Medical Record' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Education Records Modal -->
    <div v-if="showEducationModal && selectedPersonnel" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Education Records - {{ selectedPersonnel.personel_name }} {{ selectedPersonnel.personel_surname }}</h3>
        <div class="records-list">
          <div v-for="education in (selectedPersonnel.education || [])" :key="education.education_id" class="record-item">
            <div class="record-info">
              <strong>Education Date:</strong> {{ formatDate(education.education_date) }}<br>
              <strong>Expiration:</strong> {{ formatDate(education.education_expiration_date) }}<br>
              <strong>First Time:</strong> {{ education.first_time }}
            </div>
            <button class="btn btn-remove" @click="removeEducation(education)">Remove</button>
          </div>
          <div v-if="!selectedPersonnel.education || selectedPersonnel.education.length === 0" class="no-records">
            No education records found
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeModals" class="btn btn-close">Close</button>
          <button class="btn btn-add" @click="showNewEducationForm = true">+ Add New Education</button>
        </div>
      </div>
    </div>

    <!-- New Education Form Modal -->
    <div v-if="showNewEducationForm" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Add New Education Record</h3>
        <form @submit.prevent="createEducation" class="create-form">
          <div class="form-group">
            <label for="education_date">Education Date *</label>
            <input 
              type="date" 
              id="education_date"
              v-model="newEducation.education_date" 
              required
            >
          </div>
          <div class="form-group">
            <label for="first_time">First Time</label>
            <select id="first_time" v-model="newEducation.first_time">
              <option value="Yes">Yes</option>
              <option value="No">No</option>
            </select>
          </div>
          <div class="form-actions">
            <button type="button" @click="showNewEducationForm = false" class="btn btn-cancel">
              Cancel
            </button>
            <button type="submit" class="btn btn-submit" :disabled="creatingEducation">
              {{ creatingEducation ? 'Creating...' : 'Create Education Record' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- X-Ray Records Modal -->
    <div v-if="showXrayModal && selectedPersonnel" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>X-Ray Records - {{ selectedPersonnel.personel_name }} {{ selectedPersonnel.personel_surname }}</h3>
        <div class="records-list">
          <div v-for="xray in (selectedPersonnel.xrays || [])" :key="xray.xray_id" class="record-item">
            <div class="record-info">
              <strong>X-Ray Date:</strong> {{ formatDate(xray.xrays_date) }}<br>
              <strong>Expiration:</strong> {{ formatDate(xray.xrays_expiration_date) }}
            </div>
            <button class="btn btn-remove" @click="removeXray(xray)">Remove</button>
          </div>
          <div v-if="!selectedPersonnel.xrays || selectedPersonnel.xrays.length === 0" class="no-records">
            No X-Ray records found
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeModals" class="btn btn-close">Close</button>
          <button class="btn btn-add" @click="showNewXrayForm = true">+ Add New X-Ray</button>
        </div>
      </div>
    </div>

    <!-- New X-Ray Form Modal -->
    <div v-if="showNewXrayForm" class="modal-overlay">
      <div class="modal-content" @click.stop>
        <h3>Add New X-Ray Record</h3>
        <form @submit.prevent="createXray" class="create-form">
          <div class="form-group">
            <label for="xrays_date">X-Ray Date *</label>
            <input 
              type="date" 
              id="xrays_date"
              v-model="newXray.xrays_date" 
              required
            >
          </div>
          <div class="form-actions">
            <button type="button" @click="showNewXrayForm = false" class="btn btn-cancel">
              Cancel
            </button>
            <button type="submit" class="btn btn-submit" :disabled="creatingXray">
              {{ creatingXray ? 'Creating...' : 'Create X-Ray Record' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Personel Info Modal -->
<div v-if="showPersonelInfo && selectedPersonnel" class="modal-overlay personel-info-overlay">
  <div class="modal-content personel-info-modal" @click.stop>
    <PersonelInfo 
      :personnel="selectedPersonnel"
      @close="closePersonelInfo"
      @personnel-updated="handlePersonnelUpdated"
      @open-project-card="openProjectCard"
    />
  </div>
</div>

<!-- Add this before closing template tag -->
<!-- Project Info Modal -->
<div v-if="showProjectInfo" class="modal-overlay">
  <div class="modal-content large-modal" @click.stop>
    <ProjectInfo 
      :project="selectedProject"
      :personnel="selectedProjectPersonnel"
      :vehicles="selectedProjectVehicles"
      @close="closeProjectInfo"
      @edit-project="handleProjectEdit"
      @edit-personnel="handlePersonnelManagement"
      @edit-vehicles="handleVehiclesManagement"
      @project-updated="handleProjectUpdated"
      @open-personnel-card="openPersonnelCardFromProject"
    />
  </div>
</div>

<!-- Add these before closing template tag -->
<!-- Add Personnel Modal -->
<div v-if="showPersonnelForm" class="modal-overlay" @click="closePersonnelForm">
  <div class="modal-content add-personnel-form" @click.stop>
    <AddPersonel 
      :project="selectedProject"
      @close="closePersonnelForm"
      @personnelUpdated="handleProjectUpdated"
    />
  </div>
</div>

<!-- Add Vehicles Modal -->
<div v-if="showVehiclesForm" class="modal-overlay" @click="closeVehiclesForm">
  <div class="modal-content add-vehicles-form" @click.stop>
    <AddVehicles 
      :project="selectedProject" 
      @close="closeVehiclesForm"
      @vehiclesUpdated="handleProjectUpdated"
    />
  </div>
</div>

    <!-- View Personnel Modal -->
    <div v-if="showViewModal && selectedPersonnel" class="modal-overlay">
      <div class="modal-content large-modal" @click.stop>
        <h3>View Personnel - {{ selectedPersonnel.personel_name }} {{ selectedPersonnel.personel_surname }}</h3>
        <!-- Add detailed view form here -->
        <div class="form-actions">
          <button @click="closeModals" class="btn btn-close">Close</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      Loading personnel data...
    </div>

    <div v-if="error" class="error">
      <div class="error-icon">⚠️</div>
      <h3>Failed to load personnel data</h3>
      <p>Please try again later</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import PersonelInfo from './PersonelInfo.vue';
import ProjectInfo from './ProjectInfo.vue';
import AddPersonel from './AddPersonel.vue';  // Add this
import AddVehicles from './AddVehicles.vue';

export default {
  name: 'PersonnelPage',
  components:{
    PersonelInfo,
    ProjectInfo,
        AddPersonel,  // Add this
    AddVehicles 
  },
  data() {
    return {
      personnel: [],
      loading: false,
      error: false,
      showPersonelInfo: false,
          showProjectInfo: false,
    selectedProject: null,
    selectedProjectPersonnel: [],
    selectedProjectVehicles: [],
    fromProjectInfo: false,
    showVehiclesForm: false,
    showPersonnelForm: false,
      filters: {
        search: '',
        validity: '',
        role: ''
      },
      // Modal states
      showCreateModal: false,
      showViewModal: false,
      showEditModal: false,
      showMedicalModal: false,
      showEducationModal: false,
      showXrayModal: false,
      showNewMedicalForm: false,
      showNewEducationForm: false,
      showNewXrayForm: false,
      selectedPersonnel: null,
      creating: false,
      updating: false,
      deleting: false,
      creatingMedical: false,
      creatingEducation: false,
      creatingXray: false,
      // New personnel data
      newPersonnel: {
        personel_name: '',
        personel_surname: '',
        role: ''
      },
      // Edit personnel data
      editPersonnelData: {
        personel_name: '',
        personel_surname: '',
        role: ''
      },
      // New record data
      newMedical: {
        exam_date: '',
        personel_id: null
      },
      newEducation: {
        education_date: '',
        first_time: 'Yes',
        personel_id: null
      },
      newXray: {
        xrays_date: '',
        personel: null
      }
    }
  },
  computed: {
    roles() {
      return [...new Set(this.personnel.map(p => p.role))].filter(role => role);
    },
    filteredPersonnel() {
      return this.personnel.filter(person => {
        // Search filter
        const searchLower = this.filters.search.toLowerCase();
        const matchesSearch = 
          person.personel_name?.toLowerCase().includes(searchLower) ||
          person.personel_surname?.toLowerCase().includes(searchLower) ||
          person.role?.toLowerCase().includes(searchLower);
        
        // Validity filter
        const matchesValidity = !this.filters.validity || 
                              this.getValidityStatus(person) === this.filters.validity;
        
        // Role filter
        const matchesRole = !this.filters.role || person.role === this.filters.role;
        
        return matchesSearch && matchesValidity && matchesRole;
      });
    }
  },
  methods: {

    async openPersonnelCardFromProject(person) {
    try {
      // Get basic personnel data with all their records
      const personnelResponse = await axios.get(`http://localhost:8000/personel/${person.personel_id}`);
      
      // Get personnel's projects
      const projectsResponse = await axios.get(`http://localhost:8000/personel/${person.personel_id}/projects`);
      
      // Get full project details for each assigned project
      const projectPromises = projectsResponse.data.map(async (project) => {
        const fullProjectResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}`);
        return {
          ...fullProjectResponse.data,
          date_start: fullProjectResponse.data.date_start,
          duration: parseInt(fullProjectResponse.data.duration) || 0
        };
      });
      
      const projects = await Promise.all(projectPromises);
      
      // Combine personnel data with projects
      this.selectedPersonnel = {
        ...personnelResponse.data,
        projects: projects
      };
      
      this.showProjectInfo = false; // Hide ProjectInfo
      this.showPersonelInfo = true; // Show PersonelInfo
    } catch (error) {
      console.error("Failed to load personnel data", error);
      alert('Failed to load personnel details. Please try again.');
    }
  }, 

  async openProjectCard(project) {
    try {
      this.selectedProject = project;
      
      // Fetch assigned personnel
      const personnelResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}/personel`);
      this.selectedProjectPersonnel = personnelResponse.data;
      
      // Fetch assigned vehicles
      const vehiclesResponse = await axios.get(`http://localhost:8000/projects/${project.project_id}/vehicles`);
      this.selectedProjectVehicles = vehiclesResponse.data;
      
      this.showProjectInfo = true;
      this.showPersonelInfo = false; // Close PersonelInfo if open
    } catch (error) {
      console.error('Error fetching project details:', error);
      this.selectedProjectPersonnel = [];
      this.selectedProjectVehicles = [];
      this.showProjectInfo = true;
    }
  },

  closeProjectInfo() {
    this.showProjectInfo = false;
    this.selectedProject = null;
    this.selectedProjectPersonnel = [];
    this.selectedProjectVehicles = [];
  },

  handleProjectEdit(project) {
    this.selectedProject = project;
    // Additional edit logic if needed
  },

  handlePersonnelManagement() {
    this.showProjectInfo = false;
    this.showPersonnelForm = true;
    this.fromProjectInfo = true;
  },

  handleVehiclesManagement() {
    this.showProjectInfo = false;
    this.showVehiclesForm = true;
    this.fromProjectInfo = true;
  },

  handleProjectUpdated() {
    if (this.selectedProject) {
      this.openProjectCard(this.selectedProject); // Refresh data
    }
  },

  closePersonnelForm() {
    this.showPersonnelForm = false;
    if (this.fromProjectInfo) {
      this.openProjectCard(this.selectedProject);
      this.fromProjectInfo = false;
    }
  },

  closeVehiclesForm() {
    this.showVehiclesForm = false;
    if (this.fromProjectInfo) {
      this.openProjectCard(this.selectedProject);
      this.fromProjectInfo = false;
    }
  },


    
    handleMedicalCreated(newMedical) {
  // Update the selected personnel data
  if (!this.selectedPersonnel.medicals) {
    this.selectedPersonnel.medicals = [];
  }
  this.selectedPersonnel.medicals.push(newMedical);
  
  // Force the PersonelInfo to refresh by re-triggering the prop
  this.$forceUpdate();
},

handleXrayCreated(newXray) {
  if (!this.selectedPersonnel.xrays) {
    this.selectedPersonnel.xrays = [];
  }
  this.selectedPersonnel.xrays.push(newXray);
  this.$forceUpdate();
},

handleEducationCreated(newEducation) {
  if (!this.selectedPersonnel.education) {
    this.selectedPersonnel.education = [];
  }
  this.selectedPersonnel.education.push(newEducation);
  this.$forceUpdate();
},

handleMedicalRemoved(medicalId) {
  this.selectedPersonnel.medicals = this.selectedPersonnel.medicals.filter(m => m.exams_id !== medicalId);
  this.$forceUpdate();
},

handleXrayRemoved(xrayId) {
  this.selectedPersonnel.xrays = this.selectedPersonnel.xrays.filter(x => x.xray_id !== xrayId);
  this.$forceUpdate();
},

handleEducationRemoved(educationId) {
  this.selectedPersonnel.education = this.selectedPersonnel.education.filter(e => e.education_id !== educationId);
  this.$forceUpdate();
},

handlePersonelUpdated(updatedPersonnel) {
  // Update the selected personnel with new data
  Object.assign(this.selectedPersonnel, updatedPersonnel);
  // Refresh the main list
  this.getPersonnel();
},
    // Open Personel Info
viewPersonnel(person) {
  this.selectedPersonnel = person;
  this.showPersonelInfo = true;
},

// Close Personel Info
closePersonelInfo() {
  this.showPersonelInfo = false;
  this.selectedPersonnel = null;
},

// Handle personnel updates from PersonelInfo component
handlePersonnelUpdated() {
  this.getPersonnel(); // Refresh the list to show updated data
  this.closePersonelInfo();
},

// Handle project card opening from PersonelInfo
// openProjectCard(project) {
//   // You can implement this based on your existing project opening logic
//   console.log('Open project card:', project);
//   // this.$emit('open-project-card', project); // If you have a parent handler
// },
    toDateOnly(date) {
      if (!date) return null;
      return new Date(date).toISOString().split("T")[0]; // "YYYY-MM-DD"
    },
    async getPersonnel() {
      try {
        this.loading = true;
        this.error = false;
        const response = await axios.get("http://localhost:8000/personel");
        this.personnel = response.data;
      } catch (error) {
        console.error("API failed to load", error);
        this.error = true;
      } finally {
        this.loading = false;
      }
    },

    async createPersonnel() {
      try {
        this.creating = true;
        
        if (!this.newPersonnel.personel_name || !this.newPersonnel.personel_surname || !this.newPersonnel.role) {
          alert('Please fill in all required fields');
          return;
        }

        const response = await axios.post(
          "http://localhost:8000/personel/create", 
          this.newPersonnel
        );
        
        this.personnel.push(response.data);
        this.closeModals();
        alert('Personnel created successfully!');
        
      } catch (error) {
        console.error("Failed to create personnel", error);
        alert('Failed to create personnel. Please try again.');
      } finally {
        this.creating = false;
      }
    },

    async updatePersonnel() {
      try {
        this.updating = true;
        
        if (!this.editPersonnelData.personel_name || !this.editPersonnelData.personel_surname || !this.editPersonnelData.role) {
          alert('Please fill in all required fields');
          return;
        }

        const response = await axios.patch(
          `http://localhost:8000/personel/${this.selectedPersonnel.personel_id}/update`,
          this.editPersonnelData
        );
        
        // Update the personnel in the list
        const index = this.personnel.findIndex(p => p.personel_id === this.selectedPersonnel.personel_id);
        if (index !== -1) {
          this.personnel.splice(index, 1, response.data);
        }
        
        this.closeModals();
        alert('Personnel updated successfully!');
        
      } catch (error) {
        console.error("Failed to update personnel", error);
        alert('Failed to update personnel. Please try again.');
      } finally {
        this.updating = false;
      }
    },

    async deletePersonnel() {
      if (!confirm('Are you sure you want to delete this personnel? This action cannot be undone.')) {
        return;
      }

      try {
        this.deleting = true;
        
        await axios.post(`http://localhost:8000/personel/${this.selectedPersonnel.personel_id}/remove`);
        
        // Remove the personnel from the list
        this.personnel = this.personnel.filter(p => p.personel_id !== this.selectedPersonnel.personel_id);
        
        this.closeModals();
        alert('Personnel deleted successfully!');
        
      } catch (error) {
        console.error("Failed to delete personnel", error);
        alert('Failed to delete personnel. Please try again.');
      } finally {
        this.deleting = false;
      }
    },

    // Medical Records API
    async createMedical() {
      try {
        this.creatingMedical = true;
        this.newMedical.personel_id = this.selectedPersonnel.personel_id;
        this.newMedical.exam_date = this.toDateOnly(this.newMedical.exam_date);

        const response = await axios.post(
          "http://localhost:8000/medicals/create", 
          this.newMedical
        );

        if (!this.selectedPersonnel.medicals) {
          this.selectedPersonnel.medicals = [];
        }
        this.selectedPersonnel.medicals.push(response.data);

        this.showNewMedicalForm = false;
        this.resetNewMedical();
        alert("Medical record created successfully!");
      } catch (error) {
        console.error("Failed to create medical record", error);
        alert("Failed to create medical record. Please try again.");
      } finally {
        this.creatingMedical = false;
      }
    },

    async removeMedical(medical) {
      if (confirm('Are you sure you want to remove this medical record?')) {
        try {
          await axios.post(`http://localhost:8000/medicals/${medical.exams_id}/remove`);
          this.selectedPersonnel.medicals = this.selectedPersonnel.medicals.filter(m => m.exams_id !== medical.exams_id);
          alert('Medical record removed successfully!');
        } catch (error) {
          console.error("Failed to remove medical record", error);
          alert('Failed to remove medical record. Please try again.');
        }
      }
    },

    // Education Records API
    async createEducation() {
      try {
        this.creatingEducation = true;
        this.newEducation.personel_id = this.selectedPersonnel.personel_id;
        this.newEducation.education_date = this.toDateOnly(this.newEducation.education_date);

        const response = await axios.post(
          "http://localhost:8000/educations/create", 
          this.newEducation
        );

        if (!this.selectedPersonnel.education) {
          this.selectedPersonnel.education = [];
        }
        this.selectedPersonnel.education.push(response.data);

        this.showNewEducationForm = false;
        this.resetNewEducation();
        alert("Education record created successfully!");
      } catch (error) {
        console.error("Failed to create education record", error);
        alert("Failed to create education record. Please try again.");
      } finally {
        this.creatingEducation = false;
      }
    },

    async removeEducation(education) {
      if (confirm('Are you sure you want to remove this education record?')) {
        try {
          await axios.post(`http://localhost:8000/educations/${education.education_id}/remove`);
          this.selectedPersonnel.education = this.selectedPersonnel.education.filter(e => e.education_id !== education.education_id);
          alert('Education record removed successfully!');
        } catch (error) {
          console.error("Failed to remove education record", error);
          alert('Failed to remove education record. Please try again.');
        }
      }
    },

    // X-Ray Records API
    async createXray() {
      try {
        this.creatingXray = true;
        this.newXray.personel_id = this.selectedPersonnel.personel_id;
        this.newXray.xrays_date = this.toDateOnly(this.newXray.xrays_date);

        const response = await axios.post(
          "http://localhost:8000/xrays/create", 
          this.newXray
        );

        if (!this.selectedPersonnel.xrays) {
          this.selectedPersonnel.xrays = [];
        }
        this.selectedPersonnel.xrays.push(response.data);

        this.showNewXrayForm = false;
        this.resetNewXray();
        alert("X-Ray record created successfully!");
      } catch (error) {
        console.error("Failed to create X-Ray record", error);
        alert("Failed to create X-Ray record. Please try again.");
      } finally {
        this.creatingXray = false;
      }
    },

    async removeXray(xray) {
      if (confirm('Are you sure you want to remove this X-Ray record?')) {
        try {
          await axios.post(`http://localhost:8000/xrays/${xray.xray_id}/remove`);
          this.selectedPersonnel.xrays = this.selectedPersonnel.xrays.filter(x => x.xray_id !== xray.xray_id);
          alert('X-Ray record removed successfully!');
        } catch (error) {
          console.error("Failed to remove X-Ray record", error);
          alert('Failed to remove X-Ray record. Please try again.');
        }
      }
    },

    // Reset new record forms
    resetNewMedical() {
      this.newMedical = {
        exam_date: '',
        personel: null
      };
    },

    resetNewEducation() {
      this.newEducation = {
        education_date: '',
        first_time: 'Yes',
        personel: null
      };
    },

    resetNewXray() {
      this.newXray = {
        xrays_date: '',
        personel_id: null
      };
    },

    // Role-based styling
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

    // Expiration date calculations based on initial dates
    getMedicalExpiration(person) {
      if (!person.medicals || person.medicals.length === 0) return null;
      const latestMedical = person.medicals.reduce((latest, current) => {
        const currentDate = new Date(current.exam_date);
        return currentDate > new Date(latest.exam_date) ? current : latest;
      });
      
      const examDate = new Date(latestMedical.exam_date);
      examDate.setFullYear(examDate.getFullYear() + 1); // Medical expires after 1 year
      return examDate.toISOString().split('T')[0];
    },

    getEducationExpiration(person) {
      if (!person.education || person.education.length === 0) return null;
      const latestEducation = person.education.reduce((latest, current) => {
        const currentDate = new Date(current.education_date);
        return currentDate > new Date(latest.education_date) ? current : latest;
      });
      
      const educationDate = new Date(latestEducation.education_date);
      educationDate.setFullYear(educationDate.getFullYear() + 3); // Education expires after 3 years
      return educationDate.toISOString().split('T')[0];
    },

    getXrayExpiration(person) {
      if (!person.xrays || person.xrays.length === 0) return null;
      const latestXray = person.xrays.reduce((latest, current) => {
        const currentDate = new Date(current.xrays_date);
        return currentDate > new Date(latest.xrays_date) ? current : latest;
      });
      
      const xrayDate = new Date(latestXray.xrays_date);
      xrayDate.setFullYear(xrayDate.getFullYear() + 2); // Xray expires after 2 years
      return xrayDate.toISOString().split('T')[0];
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

    getValidityStatus(person) {
      const medicalExpiry = this.getMedicalExpiration(person);
      const educationExpiry = this.getEducationExpiration(person);
      const xrayExpiry = this.getXrayExpiration(person);

      // If any required date is missing, consider invalid
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

    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const options = { year: 'numeric', month: 'short', day: 'numeric' };
      return new Date(dateString).toLocaleDateString('en-US', options);
    },

    // Modal methods for records
    openMedicalModal(person) {
      this.selectedPersonnel = person;
      this.showMedicalModal = true;
    },

    openEducationModal(person) {
      this.selectedPersonnel = person;
      this.showEducationModal = true;
    },

    openXrayModal(person) {
      this.selectedPersonnel = person;
      this.showXrayModal = true;
    },

    // Modal methods
    // viewPersonnel(person) {
    //   this.selectedPersonnel = person;
    //   this.showViewModal = true;
    // },

    editPersonnel(person) {
      this.selectedPersonnel = person;
      // Populate edit form with current data
      this.editPersonnelData = {
        personel_name: person.personel_name,
        personel_surname: person.personel_surname,
        role: person.role
      };
      this.showEditModal = true;
    },

    closeModals() {
      this.showCreateModal = false;
      this.showViewModal = false;
      this.showEditModal = false;
      this.showMedicalModal = false;
      this.showEducationModal = false;
      this.showXrayModal = false;
      this.showNewMedicalForm = false;
      this.showPersonelInfo = false;
      this.showNewEducationForm = false;
      this.showNewXrayForm = false;
      this.selectedPersonnel = null;
      // Reset form data
      this.newPersonnel = {
        personel_name: '',
        personel_surname: '',
        role: ''
      };
      this.editPersonnelData = {
        personel_name: '',
        personel_surname: '',
        role: ''
      };
      this.resetNewMedical();
      this.resetNewEducation();
      this.resetNewXray();
    }
  },
  mounted() {
    this.getPersonnel();
  }
}
</script>

<style scoped>
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
}

/* Ensure modal overlay covers everything */
.modal-overlay {
  z-index: 1000;
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
/* Reuse all the same base styles from Project Dashboard */
.personnel-page {
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
  font-size: 0.8rem; /* Smaller font for more compact look */
  border: 1px solid #d1d5db;
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

.bordered-table td {
  padding: 6px 4px; /* Reduced padding for shorter rows */
  border-bottom: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  vertical-align: middle; /* Changed to middle for better alignment */
  height: 45px; /* Fixed height for consistent row height */
}

.bordered-table tr:hover {
  background-color: #f8fafc;
}

.cell-bordered {
  border-right: 1px solid #e5e7eb !important;
}

/* Updated Column Widths - More compact without projects column */
.col-name { width: 90px; }
.col-surname { width: 100px; }
.col-role { width: 80px; }
.col-medical { width: 100px; }
.col-education { width: 100px; }
.col-xray { width: 100px; }
.col-status { width: 80px; }
.col-actions { width: 120px; min-width: 120px; }

/* Unified Cell Styling for Role, Dates, and Status - More compact */
.role-cell, .date-cell, .status-cell {
  text-align: center;
  padding: 4px 2px; /* Reduced padding */
  border-radius: 4px; /* Smaller radius */
  display: flex;
  flex-direction: column;
  gap: 1px; /* Reduced gap */
  min-height: 35px; /* Smaller minimum height */
  justify-content: center;
  align-items: center;
  border: 1px solid transparent;
}

/* Role Colors - Same style as dates */
.role-cell {
  font-size: 0.7rem; /* Smaller font */
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
  font-size: 0.65rem; /* Smaller font */
  font-weight: 600;
}

/* Date Cell Styling */
.date-cell.valid {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
}

.date-cell.warning {
  background: #fef3c7;
  border: 1px solid #fcd34d;
}

.date-cell.expired {
  background: #fef2f2;
  border: 1px solid #fecaca;
}

.date-cell.no-date {
  background: #f3f4f6;
  border: 1px solid #d1d5db;
}

.date {
  font-weight: 500;
  color: #2d3748;
  font-size: 0.7rem; /* Smaller font */
}

.days-remaining {
  font-size: 0.6rem; /* Smaller font */
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.date-cell.valid .days-remaining {
  color: #16a34a;
}

.date-cell.warning .days-remaining {
  color: #d97706;
}

.date-cell.expired .days-remaining {
  color: #dc2626;
}

.date-cell.no-date .days-remaining {
  color: #6b7280;
}

/* Clickable date cells */
.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
}

.clickable:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Status Cell - Unified with other cells */
.status-cell {
  font-size: 0.65rem; /* Smaller font */
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-text {
  font-size: 0.6rem; /* Smaller font */
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

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.btn {
  padding: 5px 6px; /* Reduced padding */
  border: none;
  border-radius: 3px; /* Smaller radius */
  font-size: 0.65rem; /* Smaller font */
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  white-space: nowrap;
  flex: 1;
  min-width: 40px; /* Smaller minimum width */
}

.btn-edit {
  background: #f59e0b;
  color: white;
}

.btn-edit:hover {
  background: #d97706;
  transform: translateY(-1px);
}

.btn-view {
  background: #3b82f6;
  color: white;
}

.btn-view:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.btn-danger {
  background: #ef4444;
  color: white;
  padding: 8px 16px; /* Smaller padding */
  font-size: 0.8rem; /* Smaller font */
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
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
  justify-content: space-between; /* Changed to space-between for consistent close button placement */
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.btn-add {
  background: #10b981;
  color: white;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-add:hover {
  background: #059669;
}

.btn-remove {
  background: #ef4444;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 0.8rem;
  cursor: pointer;
}

.btn-remove:hover {
  background: #dc2626;
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

/* .large-modal {
  max-width: 50%;
  width: 50%;
  max-height: 90vh;
} */

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
  align-items: center;
  gap: 12px;
  justify-content: space-between; /* For delete button on left, other actions on right */
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
  gap: 20px;
}

.form-actions-right {
  display: flex;
  gap: 12px;
}

.btn-cancel, .btn-close {
  background: #6b7280;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.btn-cancel:hover, .btn-close:hover {
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
    min-width: 900px; /* Reduced min-width without projects column */
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
  
  .large-modal {
    max-width: 90%;
    width: 90%;
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
.modal-overlay .modal-content.add-personnel-form,
.modal-overlay .modal-content.add-vehicles-form {
  max-width: 45%;
  width: 45%;
  margin-top: 20px;
}
</style>