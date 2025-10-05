package com.planner.Project_Planner.controller;

import com.planner.Project_Planner.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class ControllerUsers {

    @Autowired
    private UsersService usersService;

    @GetMapping("/auth")
    public boolean login(@RequestParam String username, @RequestParam String password) {
        boolean authenticated = usersService.authenticateUser(username, password);
        if (authenticated) {
            return true;
        } else {
            return false;
        }
    }

}
