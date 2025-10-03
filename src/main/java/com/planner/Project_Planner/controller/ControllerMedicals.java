package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.repository.MedicalsRepository;
import com.planner.Project_Planner.services.MedicalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ControllerMedicals {

    @Autowired
    MedicalsService medicalsService;

    @GetMapping("/medicals")
    public List<Medicals> getMedicals(){
        return medicalsService.getMedicals();
    }

    @PostMapping("/medicals/create")
    public Medicals createMedicals(@RequestBody DTOMedicals medical){
        return medicalsService.saveMedicals(medical);
    }

    @PatchMapping("/medicals/{exams_id}/update")
    public Medicals updateMedical(
            @PathVariable Long exams_id,
            @RequestBody DTOMedicals updates) {
        return medicalsService.updateMedicals(exams_id, updates);
    }

    @PostMapping("/medicals/{exam_id}/remove")
    public void removeMedicalById(@PathVariable Long exam_id){
       medicalsService.removeMedical(exam_id);
    }

}
