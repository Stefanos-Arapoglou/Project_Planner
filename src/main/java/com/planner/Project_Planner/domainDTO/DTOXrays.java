package com.planner.Project_Planner.domainDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DTOXrays {
    private Long xray_id;
    private LocalDate xrays_date;
    private LocalDate xrays_expiration_date;
    private Long personel_id;
}
