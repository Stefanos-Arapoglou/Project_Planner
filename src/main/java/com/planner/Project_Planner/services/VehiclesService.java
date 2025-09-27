package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.entity.Medicals;
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

}
