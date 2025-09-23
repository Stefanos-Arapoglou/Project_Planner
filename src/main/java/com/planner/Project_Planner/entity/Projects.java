package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "project_id")
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
    private LocalDate date_start;
    @Column(name = "date_end")
    private LocalDate date_end;

    @ManyToMany
    @JoinTable(
            name="p_assignments",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns=@JoinColumn(name="personel_id")
    )
    @JsonManagedReference
    private Set<Personel> personel = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="v_assignments",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns=@JoinColumn(name="vehicle_id")
    )
    @JsonManagedReference
    private Set<Vehicles> vehicles;


}
