package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.services.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerProjects {

    @Autowired
    ProjectsService projectsService;

    @GetMapping("/projects")
    public List<Projects> getProject(){
        return projectsService.getProject();
    }

    @PostMapping("/create-project")
    public Projects createProject(@RequestBody DTOProjects project){
        return projectsService.saveProject(project);
    }

}
