package neostudy.dto;

import lombok.Getter;
import lombok.Setter;
import neostudy.EmploymentStatus;
import neostudy.Position;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@Setter
public class EmploymentDTO {

    EmploymentStatus employmentStatus;
    String employerINN;
    BigDecimal salary;
    Position position;
    Integer workExperienceTotal;
    Integer workExperienceCurrent;

    @Override
    public String toString() {
        return "EmploymentDTO{ " +
                "\nemploymentStatus=" + employmentStatus +
                "\n employerINN='" + employerINN + '\'' +
                "\n salary=" + salary +
                "\n position=" + position +
                "\n workExperienceTotal=" + workExperienceTotal +
                "\n workExperienceCurrent=" + workExperienceCurrent +
                "}";
    }
}
