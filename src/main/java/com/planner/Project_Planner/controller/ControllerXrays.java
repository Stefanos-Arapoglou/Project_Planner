package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOVehicles;
import com.planner.Project_Planner.domainDTO.DTOXrays;
import com.planner.Project_Planner.entity.Vehicles;
import com.planner.Project_Planner.entity.Xrays;
import com.planner.Project_Planner.repository.XraysRepository;
import com.planner.Project_Planner.services.XraysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ControllerXrays {

    @Autowired
    XraysService xraysService;

    @GetMapping("/xrays")
    public List<Xrays> getXrays(){
        return xraysService.getXrays();}

    @PostMapping("/xrays/create")
    public Xrays createXray(@RequestBody DTOXrays xray){
        return xraysService.saveXrays(xray);
    }

    @PatchMapping("/xrays/{xray_id}/update")
    public Xrays updateXray(
            @PathVariable Long xray_id,
            @RequestBody DTOXrays updates) {
        return xraysService.updateXray(xray_id, updates);
    }

    @PostMapping("/xrays/{xray_id}/remove")
    public void removeXrayById(@PathVariable Long xray_id){
        xraysService.removeXray(xray_id);
    }


}
