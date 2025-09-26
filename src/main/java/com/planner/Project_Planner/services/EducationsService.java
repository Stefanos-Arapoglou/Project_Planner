package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.EducationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationsService {

    @Autowired
    EducationsRepository educationsRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Educations> getEducations(){
        return educationsRepository.findAll();
    }

    public Educations saveEducation(DTOEducations education){
        return educationsRepository.save(mapDTO.DTOEducationsToEducations(education));
    }

}
