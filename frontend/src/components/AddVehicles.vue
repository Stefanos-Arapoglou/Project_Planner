<template>
    <div class="add-vehicles-page">
        <header class="page-header">
            <div class="header-content">
                <div class="project-info">
                    <span class="project-code">{{ project?.project_code }}</span>
                    <span class="project-name">{{ project?.project_name }}</span>
                </div>
            </div>
            <button class="btn btn-close" @click="closeModal" title="Close">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M18 6L6 18M6 6l12 12"></path>
                </svg>
            </button>
        </header>

        <div class="vehicles-management">
            <!-- Assigned Vehicles by Type -->
            <div class="type-section">
                <div class="section-header assigned-header">
                    <h3>Assigned to Project</h3>
                    <div class="type-stats">
                        <span class="stat ix-stat">IX: {{ assignedIX.length }}</span>
                        <span class="stat transit-stat">Transit: {{ assignedTransit.length }}</span>
                        <span class="stat crane-stat">Crane: {{ assignedCrane.length }}</span>
                    </div>
                </div>
                <div class="type-pools">
                    <div class="type-pool">
                        <h4 class="type-title ix-title">IX ({{ assignedIX.length }})</h4>
                        <div class="vehicles-list assigned-list ix-list" @drop="onDrop($event, 'assigned', 'IX')"
                            @dragover="onDragOver" @dragenter="onDragEnter">
                            <div v-for="vehicle in assignedIX" :key="vehicle.vehicle_id" class="vehicle-item"
                                draggable="true" @dragstart="onDragStart($event, vehicle, 'assigned')"
                                @dragend="onDragEnd">
                                <div class="vehicle-info">
                                    <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                                    <span class="vehicle-type">{{ vehicle.type }}</span>
                                </div>
                                <span class="vehicle-status assigned">ASSIGNED</span>
                            </div>
                            <div v-if="assignedIX.length === 0" class="empty-state">
                                No IX vehicles assigned
                            </div>
                        </div>
                    </div>

                    <div class="type-pool">
                        <h4 class="type-title transit-title">Transit ({{ assignedTransit.length }})</h4>
                        <div class="vehicles-list assigned-list transit-list"
                            @drop="onDrop($event, 'assigned', 'Transit')" @dragover="onDragOver"
                            @dragenter="onDragEnter">
                            <div v-for="vehicle in assignedTransit" :key="vehicle.vehicle_id" class="vehicle-item"
                                draggable="true" @dragstart="onDragStart($event, vehicle, 'assigned')"
                                @dragend="onDragEnd">
                                <div class="vehicle-info">
                                    <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                                    <span class="vehicle-type">{{ vehicle.type }}</span>
                                </div>
                                <span class="vehicle-status assigned">ASSIGNED</span>
                            </div>
                            <div v-if="assignedTransit.length === 0" class="empty-state">
                                No transit vehicles assigned
                            </div>
                        </div>
                    </div>

                    <div class="type-pool">
                        <h4 class="type-title crane-title">Crane ({{ assignedCrane.length }})</h4>
                        <div class="vehicles-list assigned-list crane-list" @drop="onDrop($event, 'assigned', 'Crane')"
                            @dragover="onDragOver" @dragenter="onDragEnter">
                            <div v-for="vehicle in assignedCrane" :key="vehicle.vehicle_id" class="vehicle-item"
                                draggable="true" @dragstart="onDragStart($event, vehicle, 'assigned')"
                                @dragend="onDragEnd">
                                <div class="vehicle-info">
                                    <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                                    <span class="vehicle-type">{{ vehicle.type }}</span>
                                </div>
                                <span class="vehicle-status assigned">ASSIGNED</span>
                            </div>
                            <div v-if="assignedCrane.length === 0" class="empty-state">
                                No crane vehicles assigned
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Available Vehicles by Type -->
            <div class="type-section">
                <div class="section-header available-header">
                    <h3>Available Vehicles</h3>
                    <div class="type-stats">
                        <span class="stat ix-stat">IX: {{ availableIX.length }}</span>
                        <span class="stat transit-stat">Transit: {{ availableTransit.length }}</span>
                        <span class="stat crane-stat">Crane: {{ availableCrane.length }}</span>
                    </div>
                </div>
                <div class="type-pools">
                    <div class="type-pool">
                        <h4 class="type-title ix-title">IX ({{ availableIX.length }})</h4>
                        <div class="vehicles-list available-list ix-list" @drop="onDrop($event, 'available', 'IX')"
                            @dragover="onDragOver" @dragenter="onDragEnter">
                            <div v-for="vehicle in availableIX" :key="vehicle.vehicle_id" class="vehicle-item"
                                draggable="true" @dragstart="onDragStart($event, vehicle, 'available')"
                                @dragend="onDragEnd">
                                <div class="vehicle-info">
                                    <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                                    <span class="vehicle-type">{{ vehicle.type }}</span>
                                </div>
                                <span class="vehicle-status available">AVAILABLE</span>
                            </div>
                            <div v-if="availableIX.length === 0" class="empty-state">
                                No IX vehicles available
                            </div>
                        </div>
                    </div>

                    <div class="type-pool">
                        <h4 class="type-title transit-title">Transit ({{ availableTransit.length }})</h4>
                        <div class="vehicles-list available-list transit-list"
                            @drop="onDrop($event, 'available', 'Transit')" @dragover="onDragOver"
                            @dragenter="onDragEnter">
                            <div v-for="vehicle in availableTransit" :key="vehicle.vehicle_id" class="vehicle-item"
                                draggable="true" @dragstart="onDragStart($event, vehicle, 'available')"
                                @dragend="onDragEnd">
                                <div class="vehicle-info">
                                    <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                                    <span class="vehicle-type">{{ vehicle.type }}</span>
                                </div>
                                <span class="vehicle-status available">AVAILABLE</span>
                            </div>
                            <div v-if="availableTransit.length === 0" class="empty-state">
                                No transit vehicles available
                            </div>
                        </div>
                    </div>

                    <div class="type-pool">
                        <h4 class="type-title crane-title">Crane ({{ availableCrane.length }})</h4>
                        <div class="vehicles-list available-list crane-list"
                            @drop="onDrop($event, 'available', 'Crane')" @dragover="onDragOver"
                            @dragenter="onDragEnter">
                            <div v-for="vehicle in availableCrane" :key="vehicle.vehicle_id" class="vehicle-item"
                                draggable="true" @dragstart="onDragStart($event, vehicle, 'available')"
                                @dragend="onDragEnd">
                                <div class="vehicle-info">
                                    <span class="vehicle-name">{{ vehicle.vehicle }}</span>
                                    <span class="vehicle-type">{{ vehicle.type }}</span>
                                </div>
                                <span class="vehicle-status available">AVAILABLE</span>
                            </div>
                            <div v-if="availableCrane.length === 0" class="empty-state">
                                No crane vehicles available
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="loading">
            <div class="spinner"></div>
            Loading vehicles data...
        </div>

        <!-- Action Buttons -->
        <div class="action-buttons">
            <button class="btn btn-secondary" @click="closeModal">Close</button>
        </div>
    </div>
</template>

<script>
import axios from "axios";

export default {
    name: 'AddVehicles',
    props: {
        project: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            assignedVehicles: [],
            availableVehicles: [],
            loading: false,
            draggingItem: null,
            dragSource: null
        }
    },
    computed: {

        projectId() {
            return this.project.project_id;
        },

        // Assigned vehicles by type
        assignedIX() {
            return this.assignedVehicles.filter(vehicle => vehicle.type === 'IX');
        },
        assignedTransit() {
            return this.assignedVehicles.filter(vehicle => vehicle.type === 'Transit');
        },
        assignedCrane() {
            return this.assignedVehicles.filter(vehicle => vehicle.type === 'Crane');
        },

        // Available vehicles by type
        availableIX() {
            return this.availableVehicles.filter(vehicle => vehicle.type === 'IX');
        },
        availableTransit() {
            return this.availableVehicles.filter(vehicle => vehicle.type === 'Transit');
        },
        availableCrane() {
            return this.availableVehicles.filter(vehicle => vehicle.type === 'Crane');
        }
    },
    methods: {
        closeModal() {
            console.log('Close button clicked');
            this.$emit('close');
            // Alternative close method for page navigation
        },

        async fetchVehiclesData() {
            try {
                this.loading = true;
                console.log('Fetching vehicles for project:', this.projectId);

                // Fetch assigned vehicles - you need to create this endpoint
                const assignedResponse = await axios.get(`http://localhost:8000/projects/${this.projectId}/vehicles`);
                console.log('Assigned vehicles response:', assignedResponse.data);
                this.assignedVehicles = assignedResponse.data;

                // Fetch all vehicles - you need to create this endpoint
                const allResponse = await axios.get("http://localhost:8000/vehicles");
                console.log('All vehicles response:', allResponse.data);

                const assignedIds = new Set(this.assignedVehicles.map(v => v.vehicle_id));
                this.availableVehicles = allResponse.data.filter(vehicle => !assignedIds.has(vehicle.vehicle_id));

                console.log('Available vehicles:', this.availableVehicles);
                console.log('Assigned vehicles:', this.assignedVehicles);

            } catch (error) {
                console.error("Failed to fetch vehicles data", error);
                console.error("Error details:", error.response?.data);
                alert('Failed to load vehicles data: ' + (error.response?.data?.message || error.message));
            } finally {
                this.loading = false;
            }
        },

        // Drag and Drop Methods
        onDragStart(event, vehicle, source) {
            this.draggingItem = vehicle;
            this.dragSource = source;
            event.dataTransfer.setData('text/plain', vehicle.vehicle_id);
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
            document.querySelectorAll('.vehicles-list').forEach(list => {
                list.classList.remove('drag-over');
            });
        },

        async onDrop(event, targetPool, targetType) {
            event.preventDefault();
            event.target.classList.remove('drag-over');

            if (!this.draggingItem) return;

            const vehicleId = this.draggingItem.vehicle_id;
            const sourcePool = this.dragSource;

            // Don't do anything if dropping in the same pool with same type
            if (sourcePool === targetPool && this.draggingItem.type === targetType) {
                this.draggingItem = null;
                this.dragSource = null;
                return;
            }

            try {
                if (targetPool === 'assigned') {
                    // Assign vehicle to project
                    await axios.post(`http://localhost:8000/projects/${this.projectId}/add-vehicle/${vehicleId}`);

                    // Move from available to assigned
                    const vehicleIndex = this.availableVehicles.findIndex(v => v.vehicle_id === vehicleId);
                    if (vehicleIndex > -1) {
                        const [vehicle] = this.availableVehicles.splice(vehicleIndex, 1);
                        this.assignedVehicles.push(vehicle);
                    }

                } else if (targetPool === 'available') {
                    // Remove vehicle from project
                    await axios.post(`http://localhost:8000/projects/${this.projectId}/remove-vehicle/${vehicleId}`);

                    // Move from assigned to available
                    const vehicleIndex = this.assignedVehicles.findIndex(v => v.vehicle_id === vehicleId);
                    if (vehicleIndex > -1) {
                        const [vehicle] = this.assignedVehicles.splice(vehicleIndex, 1);
                        this.availableVehicles.push(vehicle);
                    }
                }

                // Emit event to parent to refresh project data
                this.$emit('vehiclesUpdated');

            } catch (error) {
                console.error("Failed to update vehicle assignment", error);
                alert('Failed to update vehicle assignment: ' + (error.response?.data?.message || error.message));
            } finally {
                this.draggingItem = null;
                this.dragSource = null;
            }
        }
    },
    mounted() {
        this.fetchVehiclesData();
    }
}
</script>

<style scoped>
.add-vehicles-page {
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

.add-vehicles-page {
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

.vehicles-management {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
    margin-bottom: 12px;
    align-items: start;
}

.type-section {
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

.type-stats {
    display: flex;
    gap: 6px;
}

.stat {
    padding: 2px 5px;
    border-radius: 3px;
    font-size: 0.65rem;
    font-weight: 600;
}

.ix-stat {
    background: #ebf8ff;
    color: #2b6cb0;
}

.transit-stat {
    background: #f0fff4;
    color: #276749;
}

.crane-stat {
    background: #fffaf0;
    color: #c05621;
}

.type-pools {
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
}

.type-pool {
    display: flex;
    flex-direction: column;
}

.type-title {
    color: #2d3748;
    margin-bottom: 6px;
    font-size: 0.8rem;
    font-weight: 600;
    padding: 3px 0;
}

.ix-title {
    color: #2b6cb0;
    border-left: 2px solid #2b6cb0;
    padding-left: 6px;
}

.transit-title {
    color: #276749;
    border-left: 2px solid #276749;
    padding-left: 6px;
}

.crane-title {
    color: #c05621;
    border-left: 2px solid #c05621;
    padding-left: 6px;
}

.vehicles-list {
    border: 1px solid #e2e8f0;
    border-radius: 5px;
    padding: 8px;
    height: 140px;
    /* Fixed height */
    overflow-y: auto;
    background: #f8fafc;
    transition: all 0.3s ease;
    min-width: 0;
}

.vehicles-list.drag-over {
    border-color: #667eea;
    background: #edf2f7;
    box-shadow: 0 0 0 1px #667eea;
}

/* Assigned lists with different border colors */
.assigned-list.ix-list {
    border-color: #4299e1;
    background: #ebf8ff;
}

.assigned-list.transit-list {
    border-color: #48bb78;
    background: #f0fff4;
}

.assigned-list.crane-list {
    border-color: #ed8936;
    background: #fffaf0;
}

/* Available lists with different border colors */
.available-list.ix-list {
    border-color: #90cdf4;
    background: #f7fafc;
}

.available-list.transit-list {
    border-color: #9ae6b4;
    background: #f7fafc;
}

.available-list.crane-list {
    border-color: #fbd38d;
    background: #f7fafc;
}

.vehicle-item {
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

.vehicle-item:hover {
    transform: translateY(-1px);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.vehicle-item.dragging {
    opacity: 0.6;
    background: #e2e8f0;
}

.vehicle-info {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-width: 0;
    overflow: hidden;
}

.vehicle-plate {
    font-weight: 600;
    color: #2d3748;
    font-size: 0.7rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.vehicle-model {
    font-size: 0.65rem;
    color: #718096;
    text-transform: uppercase;
    letter-spacing: 0.2px;
}

.vehicle-status {
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

.vehicle-status.assigned {
    background: #c6f6d5;
    color: #276749;
}

.vehicle-status.available {
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
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
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
    .vehicles-management {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .add-vehicles-page {
        padding: 6px;
    }

    .vehicle-name {
        font-weight: 600;
        color: #2d3748;
        font-size: 0.7rem;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .vehicle-type {
        font-size: 0.65rem;
        color: #718096;
        text-transform: uppercase;
        letter-spacing: 0.2px;
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