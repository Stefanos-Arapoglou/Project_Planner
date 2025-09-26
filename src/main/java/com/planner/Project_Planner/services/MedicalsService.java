package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.MedicalsRepository;
import com.planner.Project_Planner.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalsService {

    @Autowired
    MedicalsRepository medicalsRepository;

    @Autowired
    PersonelRepository personelRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Medicals> getMedicals(){
        return medicalsRepository.findAll();
    }

    public Medicals saveMedicals(DTOMedicals dto){
        Medicals medical = mapDTO.DTOMedicalsToMedicals(dto);

        Personel personel = personelRepository.findById(dto.getPersonel_id())
                .orElseThrow(() -> new RuntimeException("Personel not found"));

        // Set relation
        medical.setPersonel(personel);

        // Save
        return medicalsRepository.save(medical);
    }

}
