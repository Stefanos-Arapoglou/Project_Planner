package com.planner.Project_Planner.domainDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DTOMedicals {
    private Long exams_id;
    private LocalDate exam_date;
    private LocalDate exam_expiration_date;
}
