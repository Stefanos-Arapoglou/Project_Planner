package com.planner.Project_Planner.domainDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class DTOProjects {
    private Long project_id;
    private String project_code;
    private String project_name;
    private String location;
    private Integer duration;
    private Integer expected_personel;
    private String crane;
    private String xy_map;
    private LocalDate date_start;
    private LocalDate date_end;

}
