package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "vehicle_id")
@Table(name="vehicles")
public class Vehicles {
    @Id
    @Column(name="vehicle_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long vehicle_id;
    private String vehicle;
    private String type;

    @ManyToMany(mappedBy = "vehicles")
    @JsonBackReference
    @JsonIgnoreProperties("vehicle")
    private Set<Projects> projects = new HashSet<>();

}
