package com.planner.Project_Planner.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "exams_id")
@Table(name = "p_medical_exams")
public class Medicals {
    @Id
    @Column(name="exams_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long exams_id;
    private LocalDate exam_date;
    private LocalDate exam_expiration_date;




}
