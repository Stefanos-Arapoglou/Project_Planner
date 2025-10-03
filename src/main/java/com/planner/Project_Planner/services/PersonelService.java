package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOPersonel;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.repository.ProjectsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PersonelService {

    @Autowired
    PersonelRepository personelRepository;

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Personel> getPersonel(){
        return personelRepository.findAll();
    }

    public Personel getPersonelById(Long personel_id){
        return personelRepository.findById(personel_id).orElse(null);
    }

    public Personel savePersonel(DTOPersonel personel){
        return personelRepository.save(mapDTO.DTOPersonelToPersonel(personel));
    }

    public Personel updatePersonel(Long id, DTOPersonel updates) {
        Personel existing = personelRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // controller will return null → JSON `null`
        }

        if (updates.getPersonel_name() != null) {
            existing.setPersonel_name(updates.getPersonel_name());
        }
        if (updates.getPersonel_surname() != null) {
            existing.setPersonel_surname(updates.getPersonel_surname());
        }
        if (updates.getRole() != null) {
            existing.setRole(updates.getRole());
        }
        return personelRepository.save(existing);
    }

    public Set<Projects> getProjectsByPersonelID(Long personel_id){
        Personel personel = personelRepository.findById(personel_id).orElseThrow(() -> new RuntimeException("Project not found with id " + personel_id));
        return personel.getProjects();
    }

    @Transactional
    public void removePersonelById(Long personel_id) {
        Personel personel = personelRepository.findById(personel_id)
                .orElseThrow(() -> new RuntimeException("Personel with id " + personel_id + " not found"));

        // Remove personel from all projects
        personel.getProjects().forEach(project -> project.getPersonel().remove(personel));

        // Optional: clear the set to avoid JPA cache issues
        personel.getProjects().clear();

        // Delete personel (cascade will remove educations, medicals, xrays)
        personelRepository.delete(personel);
    }


}
