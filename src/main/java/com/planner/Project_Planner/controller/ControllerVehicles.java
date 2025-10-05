package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.repository.VehiclesRepository;
import com.planner.Project_Planner.services.VehiclesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ControllerVehicles {

    @Autowired
    VehiclesService vehiclesService;

    @GetMapping("/vehicles")
    public List<Vehicles> getVehicles(){
            return vehiclesService.getVehicles();
    }

    @PostMapping("/vehicles/create")
    public Vehicles createVehicle(@RequestBody DTOVehicles vehicle){
        return vehiclesService.saveVehicles(vehicle);
    }

    @GetMapping("/vehicles/{vehicle_id}")
    public Vehicles getVehiclesById(@PathVariable Long vehicle_id){
        return vehiclesService.getVehicleById(vehicle_id);
    }

    @PatchMapping("/vehicles/{vehicle_id}/update")
    public Vehicles updateVehicle(
            @PathVariable Long vehicle_id,
            @RequestBody DTOVehicles updates) {
        return vehiclesService.updateVehicles(vehicle_id, updates);
    }

    @PostMapping("/vehicles/{vehicle_id}/remove")
    public void removeVehicle(@PathVariable Long vehicle_id){
        vehiclesService.removeVehicleById(vehicle_id);
    }

    @GetMapping("/vehicles/{vehicle_id}/projects")
    public Set<Projects> getProjectsByVehicleId(@PathVariable Long vehicle_id){
        return vehiclesService.getProjectsByVehicleID(vehicle_id);
    }


}
