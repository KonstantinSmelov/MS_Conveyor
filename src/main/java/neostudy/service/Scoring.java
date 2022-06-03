package neostudy.service;

import lombok.extern.slf4j.Slf4j;
import neostudy.EmploymentStatus;
import neostudy.dto.ScoringDataDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class Scoring {
    public List<String> getScoringErrorsList(ScoringDataDTO scoringDataDTO) {

        List<String> errorsList = new ArrayList<>();

        if(scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            errorsList.add("\nБезработный");
            log.info("getScoringErrorsList():  добавление в errorsList: {}", errorsList.get(errorsList.size()-1));
        }

        if(0 <= scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)))) {
            errorsList.add("\nМаленькая ЗП");
            log.info("getScoringErrorsList():  добавление в errorsList: {}", errorsList.get(errorsList.size()-1));
        }

        if(0 <= scoringDataDTO.getBirthdate().plus(20, ChronoUnit.YEARS).compareTo(LocalDate.now())) {
            errorsList.add("\nМаленький возраст");
            log.info("getScoringErrorsList():  добавление в errorsList: {}", errorsList.get(errorsList.size()-1));
        }

        if(0 >= scoringDataDTO.getBirthdate().plus(60, ChronoUnit.YEARS).compareTo(LocalDate.now())) {
            errorsList.add("\nБольшой возраст");
            log.info("getScoringErrorsList():  добавление в errorsList: {}", errorsList.get(errorsList.size()-1));
        }

        if(scoringDataDTO.getEmployment().getWorkExperienceTotal() <= 12) {
            errorsList.add("\nМаленький общий стаж");
            log.info("getScoringErrorsList():  добавление в errorsList: {}", errorsList.get(errorsList.size()-1));
        }

        if(scoringDataDTO.getEmployment().getWorkExperienceCurrent() <= 3) {
            errorsList.add("\nМаленький текущий стаж");
            log.info("getScoringErrorsList():  добавление в errorsList: {}", errorsList.get(errorsList.size()-1));
        }

        return errorsList;
    }

}
