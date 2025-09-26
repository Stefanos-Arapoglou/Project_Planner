package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOPersonel;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.services.PersonelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class ControllerPersonel {

    @Autowired
    PersonelService personelService;

    @GetMapping ("/personel")
    public List<Personel> getPersonel() {
        return personelService.getPersonel();
    }

    @PostMapping("/create-personel")
    public Personel createPersonel(@RequestBody DTOPersonel personel){
        return personelService.savePersonel(personel);
    }


}
