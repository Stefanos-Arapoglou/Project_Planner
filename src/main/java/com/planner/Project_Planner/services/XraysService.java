package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.domainDTO.DTOXrays;
import com.planner.Project_Planner.entity.Personel;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.entity.Xrays;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.PersonelRepository;
import com.planner.Project_Planner.repository.XraysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XraysService {

    @Autowired
    XraysRepository xraysRepository;

    @Autowired
    PersonelRepository personelRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Xrays> getXrays(){
        return xraysRepository.findAll();
    }

    public Xrays saveXrays(DTOXrays dtoXrays){
        Xrays xray = mapDTO.DTOXraysToXrays(dtoXrays);

        Personel personel = personelRepository.findById(dtoXrays.getPersonel_id())
                .orElseThrow(() -> new RuntimeException("Personel not found"));

        xray.setPersonel(personel);
        return xraysRepository.save(xray);
    }

    public Xrays updateXray(Long id, DTOXrays updates) {
        Xrays existing = xraysRepository.findById(id).orElse(null);
        if (existing == null) {
            return null; // controller will return null → JSON `null`
        }

        if (updates.getXrays_date() != null) {
            existing.setXrays_date(updates.getXrays_date());
        }
        if (updates.getXrays_expiration_date() != null) {
            existing.setXrays_expiration_date(updates.getXrays_expiration_date());
        }
        return xraysRepository.save(existing);
    }





}
