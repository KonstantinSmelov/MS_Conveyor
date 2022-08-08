package neostudy.service;

import neostudy.EmploymentStatus;
import neostudy.dto.EmploymentDTO;
import neostudy.dto.ScoringDataDTO;
import neostudy.exception.ScoringException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ScoringTest {

    @InjectMocks
    private Scoring scoring;
    private EmploymentDTO employmentDTO = null;
    private ScoringDataDTO scoringDataDTO = null;

    @Test
    public void scoringErrorsListWithoutUnder60() {
        employmentDTO = EmploymentDTO.builder()
                .employmentStatus(EmploymentStatus.UNEMPLOYED)
                .salary(BigDecimal.valueOf(200))
                .workExperienceCurrent(2)
                .workExperienceTotal(10).build();

        scoringDataDTO = ScoringDataDTO.builder()
                .employment(employmentDTO)
                .amount(BigDecimal.valueOf(50000))
                .birthdate(LocalDate.of(2020, 5, 15)).build();

        Assert.assertThrows(ScoringException.class, () -> scoring.checkForScoringErrors(scoringDataDTO));
    }

    @Test
    public void scoringErrorsListWithUnder60() {
        employmentDTO = EmploymentDTO.builder()
                .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
                .salary(BigDecimal.valueOf(20000))
                .workExperienceCurrent(5)
                .workExperienceTotal(13).build();
        scoringDataDTO = ScoringDataDTO.builder()
                .employment(employmentDTO)
                .amount(BigDecimal.valueOf(50000))
                .birthdate(LocalDate.of(1960, 5, 15)).build();

        Assert.assertThrows(ScoringException.class, () -> scoring.checkForScoringErrors(scoringDataDTO));
    }
}