package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.*;
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
/*
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "vehicle_id")
*/
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
    @JsonIgnore
    private Set<Projects> projects = new HashSet<>();

}
