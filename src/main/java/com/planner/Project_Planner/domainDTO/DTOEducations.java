package com.planner.Project_Planner.domainDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DTOEducations {
    private Long education_id;
    private LocalDate education_date;
    private LocalDate education_expiration_date;
    private String first_time;
}
