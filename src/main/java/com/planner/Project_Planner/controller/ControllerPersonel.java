package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOPersonel;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.services.PersonelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ControllerPersonel {

    @Autowired
    PersonelService personelService;

    @GetMapping ("/personel")
    public List<Personel> getPersonel() {
        return personelService.getPersonel();
    }

    @GetMapping("/personel/{personel_id}")
    public Personel getPersonelById(@PathVariable Long personel_id){
        return personelService.getPersonelById(personel_id);
    }

    @GetMapping("/personel/{personel_id}/projects")
    public Set<Projects> getProjectsByPersonelId(@PathVariable Long personel_id){
        return personelService.getProjectsByPersonelID(personel_id);
    }

    @PostMapping("/personel/create")
    public Personel createPersonel(@RequestBody DTOPersonel personel){
        return personelService.savePersonel(personel);
    }

    @PatchMapping("/personel/{personel_id}/update")
    public Personel updatePersonel(
            @PathVariable Long personel_id,
            @RequestBody DTOPersonel updates) {
        return personelService.updatePersonel(personel_id, updates);
    }

    @PostMapping("/personel/{personel_id}/remove")
    public void removePersonel(@PathVariable Long personel_id){
        personelService.removePersonelById(personel_id);
    }


}
