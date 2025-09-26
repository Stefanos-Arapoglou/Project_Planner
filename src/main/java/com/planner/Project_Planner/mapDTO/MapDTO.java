package com.planner.Project_Planner.mapDTO;

import com.planner.Project_Planner.domainDTO.*;
import com.planner.Project_Planner.entity.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public Personel DTOPersonelToPersonel(DTOPersonel dto){
        return Personel.builder()
                .personel_id(dto.getPersonel_id())
                .personel_name(dto.getPersonel_name())
                .personel_surname(dto.getPersonel_surname())
                .role(dto.getRole())
                .build();
    }

    public Vehicles DTOVehiclesToVehicles(DTOVehicles dto){
        return Vehicles.builder()
                .vehicle_id(dto.getVehicle_id())
                .vehicle(dto.getVehicle())
                .type(dto.getType())
                .build();
    }

    public Medicals DTOMedicalsToMedicals(DTOMedicals dto){
        return Medicals.builder()
                .exams_id(dto.getExams_id())
                .exam_date(dto.getExam_date())
                .exam_expiration_date(dto.getExam_expiration_date())
                .build();
    }

    public Educations DTOEducationsToEducations(DTOEducations dto){
        return Educations.builder()
                .education_id(dto.getEducation_id())
                .education_date(dto.getEducation_date())
                .education_expiration_date(dto.getEducation_expiration_date())
                .first_time(dto.getFirst_time())
                .build();
    }

    public Xrays DTOXraysToXrays(DTOXrays dto){
        return Xrays.builder()
                .xray_id(dto.getXray_id())
                .xrays_date(dto.getXrays_date())
                .xrays_expiration_date(dto.getXrays_expiration_date())
                .build();
    }


}
