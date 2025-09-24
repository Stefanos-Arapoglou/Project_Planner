package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.entity.Xrays;
import com.planner.Project_Planner.repository.XraysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControllerXrays {

    @Autowired
    XraysRepository xraysRepository;

    @GetMapping("/xrays")
    public List<Xrays> getXrays(){
        return xraysRepository.findAll();
    }

}
