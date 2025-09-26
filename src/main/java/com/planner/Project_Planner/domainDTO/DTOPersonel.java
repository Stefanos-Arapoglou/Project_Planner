package com.planner.Project_Planner.domainDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class DTOPersonel {
    private Long personel_id;
    private String personel_name;
    private String personel_surname;
    private String role;
    }

