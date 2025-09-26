package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.repository.MedicalsRepository;
import com.planner.Project_Planner.services.MedicalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerMedicals {

    @Autowired
    MedicalsService medicalsService;

    @GetMapping("/medicals")
    public List<Medicals> getMedicals(){
        return medicalsService.getMedicals();
    }

    @PostMapping("/create-medical")
    public Medicals createMedicals(@RequestBody DTOMedicals medical){
        return medicalsService.saveMedicals(medical);
    }

}
