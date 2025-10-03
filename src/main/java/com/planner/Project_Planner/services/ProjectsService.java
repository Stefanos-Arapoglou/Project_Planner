package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.repository.ProjectsRepository;
import com.planner.Project_Planner.repository.VehiclesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Projects getProjectById(Long project_id){
        return projectsRepository.findById(project_id).orElse(null);
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

    @Transactional
    public Projects saveVehicleToProject(Long project_id,Long vehicle_id){
        Projects project = projectsRepository.findById(project_id)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        Vehicles vehicle = vehiclesRepository.findById(vehicle_id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));

        project.getVehicles().add(vehicle);
        return projectsRepository.save(project);
    }

    @Transactional
    public Projects removeVehicleFromProject(Long project_id,Long vehicle_id){
        Projects project = projectsRepository.findById(project_id)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        project.getVehicles().removeIf(p-> p.getVehicle_id().equals(vehicle_id));
        return projectsRepository.save(project);
    }

    public Projects updateProject(Long id, DTOProjects updates) {
        Projects existing = projectsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // controller will return null → JSON `null`
        }

        if (updates.getProject_code() != null) {
            existing.setProject_code(updates.getProject_code());
        }
        if (updates.getProject_name() != null) {
            existing.setProject_name(updates.getProject_name());
        }
        if (updates.getLocation() != null) {
            existing.setLocation(updates.getLocation());
        }
        if (updates.getDuration() != null) {
            existing.setDuration(updates.getDuration());
        }
        if (updates.getExpected_personel() != null) {
            existing.setExpected_personel(updates.getExpected_personel());
        }
        if (updates.getCrane() != null) {
            existing.setCrane(updates.getCrane());
        }
        if (updates.getXy_map() != null) {
            existing.setXy_map(updates.getXy_map());
        }
        if (updates.getDate_start() != null) {
            existing.setDate_start(updates.getDate_start());
        }
        if (updates.getDate_end() != null) {
            existing.setDate_end(updates.getDate_end());
        }

        return projectsRepository.save(existing);
    }

    public Set<Personel> getPersonelByProjectId(Long project_id){
        Projects project = projectsRepository.findById(project_id).orElseThrow(() -> new RuntimeException("Project not found with id " + project_id));
        return project.getPersonel();
    }

    public Set<Vehicles> getVehiclesByProjectId(Long project_id){
        Projects project = projectsRepository.findById(project_id).orElseThrow(() -> new RuntimeException("Project not found with id " + project_id));
        return project.getVehicles();
    }

}
