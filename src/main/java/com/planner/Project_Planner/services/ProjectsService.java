package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.repository.ProjectsRepository;
import com.planner.Project_Planner.repository.VehiclesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectsService {

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    PersonelRepository personelRepository;

    @Autowired
    VehiclesRepository vehiclesRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Projects> getProject(){
        return projectsRepository.findAll();
    }

    public Projects saveProject(DTOProjects project){
        return projectsRepository.save(mapDTO.DTOProjectsToProjects(project));
    }

    @Transactional
    public Projects savePersonelToProject(Long project_id,Long personel_id){
        Projects project = projectsRepository.findById(project_id)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        Personel personel = personelRepository.findById(personel_id)
                .orElseThrow(() -> new RuntimeException("Personel not found!"));

        project.getPersonel().add(personel);
        return projectsRepository.save(project);
    }

    @Transactional
    public Projects removePersonelFromProject(Long project_id,Long personel_id){
        Projects project = projectsRepository.findById(project_id)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        project.getPersonel().removeIf(p-> p.getPersonel_id().equals(personel_id));
        return projectsRepository.save(project);
    }



}
