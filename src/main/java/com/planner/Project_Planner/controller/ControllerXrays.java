package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.domainDTO.DTOXrays;
import com.planner.Project_Planner.entity.Xrays;
import com.planner.Project_Planner.repository.XraysRepository;
import com.planner.Project_Planner.services.XraysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerXrays {

    @Autowired
    XraysService xraysService;

    @GetMapping("/xrays")
    public List<Xrays> getXrays(){
        return xraysService.getXrays();}

    @PostMapping("/create-xray")
    public Xrays createXray(@RequestBody DTOXrays xray){
        return xraysService.saveXrays(xray);
    }



}
