package com.planner.Project_Planner.repository;

import com.planner.Project_Planner.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectsRepository extends JpaRepository<Projects, Long> {
}
