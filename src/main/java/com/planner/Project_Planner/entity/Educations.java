package com.planner.Project_Planner.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "p_education")
public class Educations {
    @Id
    @Column(name="education_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long education_id;
    private String education_date;
    private String education_expiration_date;
    private String first_time;

//    @ManyToOne
//    @JoinColumn(name="personel_id")
//    private Personel personel;


}
