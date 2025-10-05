package com.planner.Project_Planner.repository;

import com.planner.Project_Planner.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {
    Users findByUsername(String username);
}
