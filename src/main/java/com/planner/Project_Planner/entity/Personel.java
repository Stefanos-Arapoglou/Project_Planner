package com.planner.Project_Planner.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Entity
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
    private Set<Projects> projects  = new HashSet<>();

//    @OneToMany(mappedBy="personel", cascade=CascadeType.ALL)
//    private List<Educations> education;


}
