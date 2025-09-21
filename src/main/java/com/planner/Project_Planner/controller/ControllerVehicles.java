package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.repository.VehiclesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerVehicles {

    @Autowired
    VehiclesRepository vehiclesRepository;

    @GetMapping("/vehicles")
        public List<Vehicles> getVehicles(){
            return vehiclesRepository.findAll();
        }

    }
