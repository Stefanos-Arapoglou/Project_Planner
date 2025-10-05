package com.planner.Project_Planner.services;

import com.planner.Project_Planner.entity.Users;
import com.planner.Project_Planner.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @Autowired
    UsersRepository usersRepository;

    public boolean authenticateUser(String username, String password){
        Users user = usersRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return true; // login successful
        }
        return false; // login failed
    }

}
