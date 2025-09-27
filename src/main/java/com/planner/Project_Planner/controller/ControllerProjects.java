package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.services.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ControllerProjects {

    @Autowired
    ProjectsService projectsService;

    @GetMapping("/projects")
    public List<Projects> getProject(){
        return projectsService.getProject();
    }


    @PostMapping("/projects/create")
    public Projects createProject(@RequestBody DTOProjects project){
        return projectsService.saveProject(project);
    }

    @PostMapping("/projects/{project_id}/add-personel/{personel_id}")
    public Projects addPersonel(@PathVariable Long project_id, @PathVariable Long personel_id) {
        return projectsService.savePersonelToProject(project_id, personel_id);
    }

    @PostMapping("/projects/{project_id}/remove-personel/{personel_id}")
    public Projects removePersonel(@PathVariable Long project_id, @PathVariable Long personel_id) {
        return projectsService.removePersonelFromProject(project_id, personel_id);
    }

    @PostMapping("/projects/{project_id}/add-vehicle/{vehicle_id}")
    public Projects addVehicle(@PathVariable Long project_id, @PathVariable Long vehicle_id) {
        return projectsService.saveVehicleToProject(project_id, vehicle_id);
    }

    @PostMapping("/projects/{project_id}/remove-vehicle/{vehicle_id}")
    public Projects removeVehicle(@PathVariable Long project_id, @PathVariable Long vehicle_id) {
        return projectsService.removeVehicleFromProject(project_id, vehicle_id);
    }

    @PatchMapping("/projects/{project_id}/update")
    public Projects updateProject(
            @PathVariable Long project_id,
            @RequestBody DTOProjects updates) {
        return projectsService.updateProject(project_id, updates);
    }

}
