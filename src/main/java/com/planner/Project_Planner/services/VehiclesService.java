package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.VehiclesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VehiclesService {

    @Autowired
    VehiclesRepository vehiclesRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Vehicles> getVehicles(){
        return vehiclesRepository.findAll();
    }

    public Vehicles getVehicleById(Long vehicle_id){
        return vehiclesRepository.findById(vehicle_id).orElse(null);
    }

    public Vehicles saveVehicles(DTOVehicles vehicle){
        return vehiclesRepository.save(mapDTO.DTOVehiclesToVehicles(vehicle));
    }

    public Vehicles updateVehicles(Long id, DTOVehicles updates) {
        Vehicles existing = vehiclesRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // controller will return null → JSON `null`
        }

        if (updates.getVehicle() != null) {
            existing.setVehicle(updates.getVehicle());
        }
        if (updates.getType() != null) {
            existing.setType(updates.getType());
        }
        return vehiclesRepository.save(existing);
    }

    @Transactional
    public void removeVehicleById(Long vehicle_id) {
        Vehicles vehicles = vehiclesRepository.findById(vehicle_id)
                .orElseThrow(() -> new RuntimeException("Vehicle with id " + vehicle_id + " not found"));

        // Remove personel from all projects
       vehicles.getProjects().forEach(project -> project.getVehicles().remove(vehicles));

        // Optional: clear the set to avoid JPA cache issues
        vehicles.getProjects().clear();

        // Delete personel (cascade will remove educations, medicals, xrays)
        vehiclesRepository.delete(vehicles);
    }

    public Set<Projects> getProjectsByVehicleID(Long vehicle_id){
        Vehicles vehicles = vehiclesRepository.findById(vehicle_id).orElseThrow(() -> new RuntimeException("Vehicle not found with id " + vehicle_id));
        return vehicles.getProjects();
    }

}
