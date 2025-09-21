package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.repository.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerProjects {

    @Autowired
    ProjectsRepository projectsRepository;

    @GetMapping("/projects")
    public List<Projects> getProjects(){
        return projectsRepository.findAll();
    }


}
