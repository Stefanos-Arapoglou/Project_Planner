<template>
  <div class="add-personnel-page">
    <header class="page-header">
      <div class="header-content">
        <div class="project-info">
          <span class="project-code">{{ project?.project_code }}</span>
          <span class="project-name">{{ project?.project_name }}</span>
        </div>
      </div>
      <button class="btn btn-close" @click="$emit('close')" title="Close">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6L6 18M6 6l12 12"></path>
        </svg>
      </button>
    </header>

    <div class="personnel-management">
      <!-- Assigned Personnel by Role -->
      <div class="role-section">
        <div class="section-header assigned-header">
          <h3>Assigned to Project</h3>
          <div class="role-stats">
            <span class="stat engineer-stat">E: {{ assignedEngineers.length }}</span>
            <span class="stat worker-stat">W: {{ assignedWorkers.length }}</span>
            <span class="stat driver-stat">D: {{ assignedDrivers.length }}</span>
          </div>
        </div>
        <div class="role-pools">
          <div class="role-pool">
            <h4 class="role-title engineer-title">Engineers ({{ assignedEngineers.length }})</h4>
            <div 
              class="personnel-list assigned-list engineer-list"
              @drop="onDrop($event, 'assigned', 'Engineer')"
              @dragover="onDragOver"
              @dragenter="onDragEnter"
            >
              <div 
                v-for="person in assignedEngineers" 
                :key="person.personel_id"
                class="personnel-item"
                draggable="true"
                @dragstart="onDragStart($event, person, 'assigned')"
                @dragend="onDragEnd"
              >
                <div class="person-info">
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <span class="person-status assigned">ASSIGNED</span>
              </div>
              <div v-if="assignedEngineers.length === 0" class="empty-state">
                No engineers assigned
              </div>
            </div>
          </div>

          <div class="role-pool">
            <h4 class="role-title worker-title">Workers ({{ assignedWorkers.length }})</h4>
            <div 
              class="personnel-list assigned-list worker-list"
              @drop="onDrop($event, 'assigned', 'Worker')"
              @dragover="onDragOver"
              @dragenter="onDragEnter"
            >
              <div 
                v-for="person in assignedWorkers" 
                :key="person.personel_id"
                class="personnel-item"
                draggable="true"
                @dragstart="onDragStart($event, person, 'assigned')"
                @dragend="onDragEnd"
              >
                <div class="person-info">
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <span class="person-status assigned">ASSIGNED</span>
              </div>
              <div v-if="assignedWorkers.length === 0" class="empty-state">
                No workers assigned
              </div>
            </div>
          </div>

          <div class="role-pool">
            <h4 class="role-title driver-title">Drivers ({{ assignedDrivers.length }})</h4>
            <div 
              class="personnel-list assigned-list driver-list"
              @drop="onDrop($event, 'assigned', 'Driver')"
              @dragover="onDragOver"
              @dragenter="onDragEnter"
            >
              <div 
                v-for="person in assignedDrivers" 
                :key="person.personel_id"
                class="personnel-item"
                draggable="true"
                @dragstart="onDragStart($event, person, 'assigned')"
                @dragend="onDragEnd"
              >
                <div class="person-info">
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <span class="person-status assigned">ASSIGNED</span>
              </div>
              <div v-if="assignedDrivers.length === 0" class="empty-state">
                No drivers assigned
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Available Personnel by Role -->
      <div class="role-section">
        <div class="section-header available-header">
          <h3>Available Personnel</h3>
          <div class="role-stats">
            <span class="stat engineer-stat">E: {{ availableEngineers.length }}</span>
            <span class="stat worker-stat">W: {{ availableWorkers.length }}</span>
            <span class="stat driver-stat">D: {{ availableDrivers.length }}</span>
          </div>
        </div>
        <div class="role-pools">
          <div class="role-pool">
            <h4 class="role-title engineer-title">Engineers ({{ availableEngineers.length }})</h4>
            <div 
              class="personnel-list available-list engineer-list"
              @drop="onDrop($event, 'available', 'Engineer')"
              @dragover="onDragOver"
              @dragenter="onDragEnter"
            >
              <div 
                v-for="person in availableEngineers" 
                :key="person.personel_id"
                class="personnel-item"
                draggable="true"
                @dragstart="onDragStart($event, person, 'available')"
                @dragend="onDragEnd"
              >
                <div class="person-info">
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <span class="person-status available">AVAILABLE</span>
              </div>
              <div v-if="availableEngineers.length === 0" class="empty-state">
                No engineers available
              </div>
            </div>
          </div>

          <div class="role-pool">
            <h4 class="role-title worker-title">Workers ({{ availableWorkers.length }})</h4>
            <div 
              class="personnel-list available-list worker-list"
              @drop="onDrop($event, 'available', 'Worker')"
              @dragover="onDragOver"
              @dragenter="onDragEnter"
            >
              <div 
                v-for="person in availableWorkers" 
                :key="person.personel_id"
                class="personnel-item"
                draggable="true"
                @dragstart="onDragStart($event, person, 'available')"
                @dragend="onDragEnd"
              >
                <div class="person-info">
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <span class="person-status available">AVAILABLE</span>
              </div>
              <div v-if="availableWorkers.length === 0" class="empty-state">
                No workers available
              </div>
            </div>
          </div>

          <div class="role-pool">
            <h4 class="role-title driver-title">Drivers ({{ availableDrivers.length }})</h4>
            <div 
              class="personnel-list available-list driver-list"
              @drop="onDrop($event, 'available', 'Driver')"
              @dragover="onDragOver"
              @dragenter="onDragEnter"
            >
              <div 
                v-for="person in availableDrivers" 
                :key="person.personel_id"
                class="personnel-item"
                draggable="true"
                @dragstart="onDragStart($event, person, 'available')"
                @dragend="onDragEnd"
              >
                <div class="person-info">
                  <span class="person-name">{{ person.personel_name }} {{ person.personel_surname }}</span>
                  <span class="person-role">{{ person.role }}</span>
                </div>
                <span class="person-status available">AVAILABLE</span>
              </div>
              <div v-if="availableDrivers.length === 0" class="empty-state">
                No drivers available
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      Loading personnel data...
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons">
      <button class="btn btn-secondary" @click="$emit('close')">Close</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";


export default {
  name: 'AddPersonel',
  props: {
    project: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      assignedPersonnel: [],
      availablePersonnel: [],
      loading: false,
      draggingItem: null,
      dragSource: null
    }
  },
  computed: {
    projectId() {
      return this.project.project_id;
    },
    
    // Assigned personnel by role
    assignedEngineers() {
      return this.assignedPersonnel.filter(person => person.role === 'Engineer');
    },
    assignedWorkers() {
      return this.assignedPersonnel.filter(person => person.role === 'Worker');
    },
    assignedDrivers() {
      return this.assignedPersonnel.filter(person => person.role === 'Driver');
    },
    
    // Available personnel by role
    availableEngineers() {
      return this.availablePersonnel.filter(person => person.role === 'Engineer');
    },
    availableWorkers() {
      return this.availablePersonnel.filter(person => person.role === 'Worker');
    },
    availableDrivers() {
      return this.availablePersonnel.filter(person => person.role === 'Driver');
    }
  },
  methods: {
    async fetchPersonnelData() {
      try {
        this.loading = true;
        
        // Fetch assigned personnel
        const assignedResponse = await axios.get(`http://localhost:8000/projects/${this.projectId}/personel`);
        this.assignedPersonnel = assignedResponse.data;
        
        // Fetch all personnel and filter out assigned ones
        const allResponse = await axios.get("http://localhost:8000/personel");
        const assignedIds = new Set(this.assignedPersonnel.map(p => p.personel_id));
        this.availablePersonnel = allResponse.data.filter(person => !assignedIds.has(person.personel_id));
        
      } catch (error) {
        console.error("Failed to fetch personnel data", error);
        alert('Failed to load personnel data');
      } finally {
        this.loading = false;
      }
    },

    // Drag and Drop Methods
    onDragStart(event, person, source) {
      this.draggingItem = person;
      this.dragSource = source;
      event.dataTransfer.setData('text/plain', person.personel_id);
      event.target.classList.add('dragging');
    },

    onDragOver(event) {
      event.preventDefault();
    },

    onDragEnter(event) {
      event.preventDefault();
      event.target.classList.add('drag-over');
    },

    onDragEnd(event) {
      event.target.classList.remove('dragging');
      document.querySelectorAll('.personnel-list').forEach(list => {
        list.classList.remove('drag-over');
      });
    },

    async onDrop(event, targetPool, targetRole) {
      event.preventDefault();
      event.target.classList.remove('drag-over');
      
      if (!this.draggingItem) return;

      const personId = this.draggingItem.personel_id;
      const sourcePool = this.dragSource;

      // Don't do anything if dropping in the same pool with same role
      if (sourcePool === targetPool && this.draggingItem.role === targetRole) {
        this.draggingItem = null;
        this.dragSource = null;
        return;
      }

      try {
        if (targetPool === 'assigned') {
          // Assign personnel to project
          await axios.post(`http://localhost:8000/projects/${this.projectId}/add-personel/${personId}`);
          
          // Move from available to assigned
          const personIndex = this.availablePersonnel.findIndex(p => p.personel_id === personId);
          if (personIndex > -1) {
            const [person] = this.availablePersonnel.splice(personIndex, 1);
            this.assignedPersonnel.push(person);
          }
          
        } else if (targetPool === 'available') {
          // Remove personnel from project
          await axios.post(`http://localhost:8000/projects/${this.projectId}/remove-personel/${personId}`);
          
          // Move from assigned to available
          const personIndex = this.assignedPersonnel.findIndex(p => p.personel_id === personId);
          if (personIndex > -1) {
            const [person] = this.assignedPersonnel.splice(personIndex, 1);
            this.availablePersonnel.push(person);
          }
        }
        
        // Emit event to parent to refresh project data
        this.$emit('personnelUpdated');
        
      } catch (error) {
        console.error("Failed to update personnel assignment", error);
        alert('Failed to update personnel assignment');
      } finally {
        this.draggingItem = null;
        this.dragSource = null;
      }
    }
  },
  mounted() {
    this.fetchPersonnelData();
  }
}
</script>

<style scoped>
.add-personnel-page {
  padding: 8px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
  /* ADD THESE LINES: */
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.add-personnel-page {
  padding: 8px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 12px 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.project-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.project-code {
  background: rgba(255, 255, 255, 0.2);
  padding: 3px 6px;
  border-radius: 4px;
  font-weight: 600;
  font-size: 1.0rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.project-name {
  font-weight: 500;
  font-size: 1.5rem;
  opacity: 0.9;
}

.page-title {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: white;
}

.btn-close {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

.personnel-management {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 12px;
  align-items: start;
}

.role-section {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e2e8f0;
}

.section-header h3 {
  margin: 0;
  font-size: 0.9rem;
  color: #2d3748;
  font-weight: 600;
}

.role-stats {
  display: flex;
  gap: 6px;
}

.stat {
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 0.65rem;
  font-weight: 600;
}

.engineer-stat { background: #ebf8ff; color: #2b6cb0; }
.worker-stat { background: #f0fff4; color: #276749; }
.driver-stat { background: #fffaf0; color: #c05621; }

.role-pools {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.role-pool {
  display: flex;
  flex-direction: column;
}

.role-title {
  color: #2d3748;
  margin-bottom: 6px;
  font-size: 0.8rem;
  font-weight: 600;
  padding: 3px 0;
}

.engineer-title { color: #2b6cb0; border-left: 2px solid #2b6cb0; padding-left: 6px; }
.worker-title { color: #276749; border-left: 2px solid #276749; padding-left: 6px; }
.driver-title { color: #c05621; border-left: 2px solid #c05621; padding-left: 6px; }

.personnel-list {
  border: 1px solid #e2e8f0;
  border-radius: 5px;
  padding: 8px;
  height: 140px; /* Fixed height */
  overflow-y: auto;
  background: #f8fafc;
  transition: all 0.3s ease;
  min-width: 0;
}

.personnel-list.drag-over {
  border-color: #667eea;
  background: #edf2f7;
  box-shadow: 0 0 0 1px #667eea;
}

/* Assigned lists with different border colors */
.assigned-list.engineer-list {
  border-color: #4299e1;
  background: #ebf8ff;
}

.assigned-list.worker-list {
  border-color: #48bb78;
  background: #f0fff4;
}

.assigned-list.driver-list {
  border-color: #ed8936;
  background: #fffaf0;
}

/* Available lists with different border colors */
.available-list.engineer-list {
  border-color: #90cdf4;
  background: #f7fafc;
}

.available-list.worker-list {
  border-color: #9ae6b4;
  background: #f7fafc;
}

.available-list.driver-list {
  border-color: #fbd38d;
  background: #f7fafc;
}

.personnel-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 6px;
  margin-bottom: 3px;
  background: white;
  border-radius: 3px;
  border: 1px solid #e2e8f0;
  cursor: move;
  transition: all 0.2s ease;
  font-size: 0.75rem;
  min-width: 0;
}

.personnel-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.personnel-item.dragging {
  opacity: 0.6;
  background: #e2e8f0;
}

.person-info {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.person-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.7rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.person-role {
  font-size: 0.65rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.2px;
}

.person-status {
  font-size: 0.6rem;
  font-weight: 600;
  padding: 2px 4px;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.2px;
  white-space: nowrap;
  margin-left: 6px;
  flex-shrink: 0;
}

.person-status.assigned {
  background: #c6f6d5;
  color: #276749;
}

.person-status.available {
  background: #fed7d7;
  color: #c53030;
}

.empty-state {
  text-align: center;
  padding: 15px 8px;
  color: #a0aec0;
  font-style: italic;
  background: white;
  border-radius: 3px;
  border: 1px solid #e2e8f0;
  font-size: 0.7rem;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #6c757d;
  font-size: 0.8rem;
}

.spinner {
  width: 25px;
  height: 25px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.btn-secondary {
  background: #6b7280;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.75rem;
}

.btn-secondary:hover {
  background: #4b5563;
}

@media (max-width: 768px) {
  .personnel-management {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .add-personnel-page {
    padding: 6px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
  
  .section-header {
    flex-direction: column;
    gap: 6px;
    align-items: flex-start;
  }
}
</style>