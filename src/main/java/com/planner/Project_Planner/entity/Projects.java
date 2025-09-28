package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
/*
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "project_id")
*/
@Table(name = "projects")
public class Projects {
    @Id
    @Column(name="project_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long project_id;
    private String project_code;
    private String project_name;
    private String location;
    private Integer duration;
    private Integer expected_personel;
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
    @JsonIgnoreProperties({"education", "medicals", "xrays", "projects"})
    private Set<Personel> personel = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="v_assignments",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns=@JoinColumn(name="vehicle_id")
    )
    @JsonIgnoreProperties({"education", "medicals", "xrays", "projects"})
    private Set<Vehicles> vehicles = new HashSet<>();


}
