package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.MedicalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalsService {

    @Autowired
    MedicalsRepository medicalsRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Medicals> getMedicals(){
        return medicalsRepository.findAll();
    }

    public Medicals saveMedicals(DTOMedicals medical){
        return medicalsRepository.save(mapDTO.DTOMedicalsToMedicals(medical));
    }

}
