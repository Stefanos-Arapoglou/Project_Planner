package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.entity.Educations;
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

        medical.setPersonel(personel);

        return medicalsRepository.save(medical);
    }

    public Medicals updateMedicals(Long id, DTOMedicals updates) {
        Medicals existing = medicalsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // controller will return null → JSON `null`
        }

        if (updates.getExam_date() != null) {
            existing.setExam_date(updates.getExam_date());
        }
        if (updates.getExam_expiration_date() != null) {
            existing.setExam_expiration_date(updates.getExam_expiration_date());
        }
        return medicalsRepository.save(existing);
    }

}
