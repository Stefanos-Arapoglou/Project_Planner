package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "personel_id")*/
/*
@JsonIgnoreProperties({"education", "medicals", "xrays"})
*/
@Table(name = "personel")
public class Personel {
    @Id
    @Column(name="personel_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long personel_id;
    private String personel_name;
    private String personel_surname;
    private String role;

    @ManyToMany(mappedBy = "personel")
    @JsonIgnore
    private Set<Projects> projects = new HashSet<>();

    @OneToMany(mappedBy="personel", cascade=CascadeType.ALL)
/*    @JsonManagedReference*/
    private List<Educations> education;

    @OneToMany(mappedBy="personel", cascade=CascadeType.ALL)
/*    @JsonManagedReference*/
    private List<Medicals> medicals;

    @OneToMany(mappedBy="personel", cascade=CascadeType.ALL)
/*
    @JsonManagedReference
*/
    private List<Xrays> xrays;

}
