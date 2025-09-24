package com.planner.Project_Planner.mapDTO;

import com.planner.Project_Planner.domainDTO.DTOProjects;
import com.planner.Project_Planner.entity.Projects;
import org.springframework.stereotype.Service;

@Service
public class MapDTO {

    public Projects DTOProjectsToProjects(DTOProjects dto){
        return Projects.builder()
                .project_id(dto.getProject_id())
                .project_code(dto.getProject_code())
                .project_name(dto.getProject_name())
                .location(dto.getLocation())
                .duration(dto.getDuration())
                .expected_personel(dto.getExpected_personel())
                .crane(dto.getCrane())
                .xy_map(dto.getXy_map())
                .date_start(dto.getDate_start())
                .date_end(dto.getDate_end())
                .build();

    }
}
