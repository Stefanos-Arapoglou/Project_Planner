package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.VehiclesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiclesService {

    @Autowired
    VehiclesRepository vehiclesRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Vehicles> getVehicles(){
        return vehiclesRepository.findAll();
    }

    public Vehicles saveVehicles(DTOVehicles vehicle){
        return vehiclesRepository.save(mapDTO.DTOVehiclesToVehicles(vehicle));
    }

}
