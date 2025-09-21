package com.planner.Project_Planner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "projects")
public class Projects {
    @Id
    @Column(name="project_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long project_id;
    private String project_code;
    private String project_name;
    private String location;
    private int duration;
    private int expected_personel;
    private String crane;
    private String xy_map;
    @Column(name = "date_start")
    private String date_start;
    @Column(name = "date_end")
    private String date_end;

}
