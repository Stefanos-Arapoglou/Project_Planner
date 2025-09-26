package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.repository.VehiclesRepository;
import com.planner.Project_Planner.services.VehiclesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerVehicles {

    @Autowired
    VehiclesService vehiclesService;

    @GetMapping("/vehicles")
    public List<Vehicles> getVehicles(){
            return vehiclesService.getVehicles();
    }

    @PostMapping("/create-vehicle")
    public Vehicles createVehicle(@RequestBody DTOVehicles vehicle){
        return vehiclesService.saveVehicles(vehicle);
    }

}
