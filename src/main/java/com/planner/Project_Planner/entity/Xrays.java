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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "xray_id")
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
    private Personel personel;


}
