package com.planner.Project_Planner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @Column(name="vehicle_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long user_id;
    private String username;
    private String password;

}
