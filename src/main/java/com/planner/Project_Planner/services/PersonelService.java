package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOPersonel;
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


}
