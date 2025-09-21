package com.planner.Project_Planner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name="vehicles")
public class Vehicles {
    @Id
    @Column(name="vehicle_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long vehicle_id;
    private String vehicle;
    private String type;

//    @ManyToMany
//    @JoinTable(
//            name="v_assignments",
//            joinColumns = @JoinColumn(name="vehicle_id"),
//            inverseJoinColumns=@JoinColumn(name="project_id")
//    )
//    private Set<Projects> projects;


}
