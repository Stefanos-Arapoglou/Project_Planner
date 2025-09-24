package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectsService {

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Projects> getProject(){
        return projectsRepository.findAll();
    }

    public Projects saveProject(DTOProjects project){
        return projectsRepository.save(mapDTO.DTOProjectsToProjects(project));
    }
}
