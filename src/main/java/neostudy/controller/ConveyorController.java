package neostudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import neostudy.exception.ScoringException;
import neostudy.dto.CreditDTO;
import neostudy.dto.LoanApplicationRequestDTO;
import neostudy.dto.LoanOfferDTO;
import neostudy.dto.ScoringDataDTO;
import neostudy.service.ConveyorService;
import neostudy.service.Scoring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/conveyor")
public class ConveyorController {

    @Autowired
    private ConveyorService conveyorService;

    @Autowired
    private Scoring scoring;

    @Operation
    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {

        log.info("@PostMapping(\"/offers\"): входящий LoanApplicationRequestDTO: {}", loanApplicationRequestDTO.toString());
        List<LoanOfferDTO> loanOfferDTOList = conveyorService.getOffers(loanApplicationRequestDTO);
        log.info("@PostMapping(\"/offers\"): возвращаемый List<LoanOfferDTO>: {}", loanOfferDTOList.toString());
        return loanOfferDTOList;
    }

    @Operation
    @PostMapping("/calculation")
    public CreditDTO calculation(@RequestBody ScoringDataDTO scoringDataDTO) throws ScoringException {

        log.info("@PostMapping(\"/calculation\"): входящий ScoringDataDTO: {}", scoringDataDTO);

        List<String> errorsList = scoring.getScoringErrorsList(scoringDataDTO);
        if(!errorsList.isEmpty()) {
            log.info("@PostMapping(\"/calculation\"): errorList: {}", errorsList);
        }

        if (errorsList.isEmpty()) {
            CreditDTO creditDTO = conveyorService.getCreditCalculation(scoringDataDTO);
            log.info("@PostMapping(\"/calculation\"): возвращаемый creditDTO: {}", creditDTO);
            return creditDTO;
        } else {
//            errorsList.add("\n");
            throw new ScoringException(errorsList);
        }
    }

}
