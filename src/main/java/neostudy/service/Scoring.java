package neostudy.service;

import neostudy.EmploymentStatus;
import neostudy.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Scoring {
    public static boolean isScoringPass(ScoringDataDTO scoringDataDTO) {
        return scoringDataDTO.getEmployment().getEmploymentStatus() != EmploymentStatus.UNEMPLOYED
                && 0 >= scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)))
                && 0 >= scoringDataDTO.getBirthdate().plus(20, ChronoUnit.YEARS).compareTo(LocalDate.now())
                && 0 <= scoringDataDTO.getBirthdate().plus(60, ChronoUnit.YEARS).compareTo(LocalDate.now())
                && scoringDataDTO.getEmployment().getWorkExperienceTotal() >= 12
                && scoringDataDTO.getEmployment().getWorkExperienceCurrent() >= 3;
    }
}
