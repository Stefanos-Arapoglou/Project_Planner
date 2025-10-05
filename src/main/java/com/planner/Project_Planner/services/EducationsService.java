package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOEducations;
import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Educations;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Projects;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.EducationsRepository;
import com.planner.Project_Planner.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EducationsService {

    @Autowired
    EducationsRepository educationsRepository;

    @Autowired
    MapDTO mapDTO;

    @Autowired
    PersonelRepository personelRepository;

    public List<Educations> getEducations(){
        return educationsRepository.findAll();
    }

    public Educations saveEducation(DTOEducations dtoEducations){
        Educations education = mapDTO.DTOEducationsToEducations(dtoEducations);

        Personel personel = personelRepository.findById(dtoEducations.getPersonel_id())
                .orElseThrow(() -> new RuntimeException("Personel not found"));

        education.setPersonel(personel);
        return educationsRepository.save(education);
    }

    public Educations updateEducation(Long id, DTOEducations updates) {
        Educations existing = educationsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // controller will return null → JSON `null`
        }

        if (updates.getEducation_date() != null) {
            existing.setEducation_date(updates.getEducation_date());
        }
        if (updates.getEducation_expiration_date() != null) {
            existing.setEducation_expiration_date(updates.getEducation_expiration_date());
        }
        if (updates.getFirst_time() != null) {
            existing.setFirst_time(updates.getFirst_time());
        }
        return educationsRepository.save(existing);
    }

    public void removeEducaton(Long education_id) {
        educationsRepository.deleteById(education_id);
    }

    public Educations createEducations(DTOEducations dto) {
        Personel person = personelRepository.findById(dto.getPersonel_id())
                .orElseThrow(() -> new RuntimeException("Personel not found"));

        Educations educations = new Educations();
        educations.setEducation_date(dto.getEducation_date());
        educations.setEducation_expiration_date(dto.getEducation_date().plusYears(3));
        educations.setFirst_time(dto.getFirst_time());// example
        educations.setPersonel(person); // <--- link to person

        return educationsRepository.save(educations);
    }

}
