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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "xray_id")
*/
@Table(name = "p_medical_xrays")
public class Xrays {
    @Id
    @Column(name="xray_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long xray_id;
    private LocalDate xrays_date;
    private LocalDate xrays_expiration_date;

    @ManyToOne
    @JoinColumn(name="personel_id")
    @JsonBackReference
    @JsonIgnore
    private Personel personel;


}
