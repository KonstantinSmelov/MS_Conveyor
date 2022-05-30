package neostudy.service;

import neostudy.EmploymentStatus;
import neostudy.dto.EmploymentDTO;
import neostudy.dto.ScoringDataDTO;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ScoringTest {

    @Test
    public void isScoringPassTest() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        EmploymentDTO employmentDTO = new EmploymentDTO();

        scoringDataDTO.setAmount(BigDecimal.valueOf(50000));
        scoringDataDTO.setBirthdate(LocalDate.of(2000, 5, 28));
        employmentDTO.setSalary(BigDecimal.valueOf(30000));
        employmentDTO.setWorkExperienceCurrent(10);
        employmentDTO.setWorkExperienceTotal(15);
        employmentDTO.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        scoringDataDTO.setEmployment(employmentDTO);
        Assert.assertFalse(Scoring.isScoringPass(scoringDataDTO));

        employmentDTO.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        Assert.assertTrue(Scoring.isScoringPass(scoringDataDTO));

        employmentDTO.setWorkExperienceTotal(5);
        Assert.assertFalse(Scoring.isScoringPass(scoringDataDTO));
    }

}
