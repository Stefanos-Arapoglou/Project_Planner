package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.repository.EducationsRepository;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.services.EducationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ControllerEducations {

    @Autowired
    EducationsService educationsService;

    @GetMapping("/educations")
    public List<Educations> getEducations(){
        return educationsService.getEducations();
    }

    @PostMapping("/educations/create")
    public Educations createEducations(@RequestBody DTOEducations educations){
        return educationsService.saveEducation(educations);
    }

    @PatchMapping("/educations/{education_id}/update")
    public Educations updateEducation(
            @PathVariable Long education_id,
            @RequestBody DTOEducations updates) {
        return educationsService.updateEducation(education_id, updates);
    }

    @PostMapping("/educations/{education_id}/remove")
    public void removeEducationById(@PathVariable Long education_id){
        educationsService.removeEducaton(education_id);
    }


}
