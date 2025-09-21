package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.repository.EducationsRepository;
import com.planner.Project_Planner.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerEducations {

    @Autowired
    EducationsRepository educationsRepository;


    @GetMapping("/educations")
    public List<Educations> getEducations(){
        return educationsRepository.findAll();
    }



}
