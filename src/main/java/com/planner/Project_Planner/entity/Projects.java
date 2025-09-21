package com.planner.Project_Planner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name="p_assignments",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns=@JoinColumn(name="personel_id")
    )
    private Set<Personel> personel  = new HashSet<>();

//    @ManyToMany
//    @JoinTable(
//            name="v_assignments",
//            joinColumns = @JoinColumn(name="project_id"),
//            inverseJoinColumns=@JoinColumn(name="vehicle_id")
//    )
//    private Set<Vehicles> vehicles;


}
