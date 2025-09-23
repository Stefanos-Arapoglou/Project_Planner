package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.repository.MedicalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerMedicals {

    @Autowired
    MedicalsRepository medicalsRepository;

    @GetMapping("/medicals")
    public List<Medicals> getMedicals(){
        return medicalsRepository.findAll();
    }

}
