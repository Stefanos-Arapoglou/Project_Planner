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

}
