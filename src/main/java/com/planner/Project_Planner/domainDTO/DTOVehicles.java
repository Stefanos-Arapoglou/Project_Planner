package com.planner.Project_Planner.domainDTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DTOVehicles {
    private Long vehicle_id;
    private String vehicle;
    private String type;
}
