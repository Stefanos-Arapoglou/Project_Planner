package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.repository.EducationsRepository;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.services.EducationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ControllerEducations {

    @Autowired
    EducationsService educationsService;

    @GetMapping("/educations")
    public List<Educations> getEducations(){
        return educationsService.getEducations();
    }

    @PostMapping("/create-education")
    public Educations createEducations(@RequestBody DTOEducations educations){
        return educationsService.saveEducation(educations);
    }



}
