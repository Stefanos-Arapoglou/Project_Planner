package com.planner.Project_Planner.services;

import com.planner.Project_Planner.domainDTO.DTOXrays;
import com.planner.Project_Planner.entity.Xrays;
import com.planner.Project_Planner.mapDTO.MapDTO;
import com.planner.Project_Planner.repository.XraysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XraysService {

    @Autowired
    XraysRepository xraysRepository;

    @Autowired
    MapDTO mapDTO;

    public List<Xrays> getXrays(){
        return xraysRepository.findAll();
    }

    public Xrays saveXrays(DTOXrays xrays){
        return xraysRepository.save(mapDTO.DTOXraysToXrays(xrays));
    }
}
