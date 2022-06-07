package neostudy.dto;

import lombok.*;
import neostudy.EmploymentStatus;
import neostudy.Position;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDTO {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
