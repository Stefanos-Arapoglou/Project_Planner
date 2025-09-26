package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.EducationsRepository;
import com.planner.Project_Planner.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationsService {

    @Autowired
    EducationsRepository educationsRepository;

    @Autowired
    MapDTO mapDTO;

    @Autowired
    PersonelRepository personelRepository;

    public List<Educations> getEducations(){
        return educationsRepository.findAll();
    }

    public Educations saveEducation(DTOEducations dtoEducations){
//        return educationsRepository.save(mapDTO.DTOEducationsToEducations(education));
        Educations education = mapDTO.DTOEducationsToEducations(dtoEducations);

        Personel personel = personelRepository.findById(dtoEducations.getPersonel_id())
                .orElseThrow(() -> new RuntimeException("Personel not found"));

        education.setPersonel(personel);
        return educationsRepository.save(education);

    }

}
