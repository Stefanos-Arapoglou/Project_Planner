package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
/*
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "exams_id")
*/
@Table(name = "p_medical_exams")
public class Medicals {
    @Id
    @Column(name="exams_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long exams_id;
    private LocalDate exam_date;
    private LocalDate exam_expiration_date;

    @ManyToOne
    @JoinColumn(name="personel_id")
    @JsonBackReference
    @JsonIgnore
    private Personel personel;


}
