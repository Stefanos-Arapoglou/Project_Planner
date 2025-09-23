package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class ControllerPersonel {

    @Autowired
    PersonelRepository personelRepository;

    @GetMapping ("/personel")
    public List<Personel> getPersonel(){;
            return personelRepository.findAll();
    }


}
