package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "education_id")
@Table(name = "p_education")
public class Educations {
    @Id
    @Column(name="education_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long education_id;
    private LocalDate education_date;
    private LocalDate education_expiration_date;
    private String first_time;

    @ManyToOne
    @JoinColumn(name="personel_id")
    private Personel personel;


}
