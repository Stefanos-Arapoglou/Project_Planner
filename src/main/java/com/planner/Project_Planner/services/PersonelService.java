package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOMedicals;
import com.planner.Project_Planner.domainDTO.DTOPersonel;
import com.planner.Project_Planner.entity.Medicals;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonelService {

    @Autowired
    PersonelRepository personelRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Personel> getPersonel(){
        return personelRepository.findAll();
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


}
