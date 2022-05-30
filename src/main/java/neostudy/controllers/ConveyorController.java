package neostudy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import neostudy.advices.ScoringException;
import neostudy.dto.CreditDTO;
import neostudy.dto.LoanApplicationRequestDTO;
import neostudy.dto.LoanOfferDTO;
import neostudy.dto.ScoringDataDTO;
import neostudy.service.ConveyorService;
import neostudy.service.Scoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
public class ConveyorController {

    private static final Logger logger = LoggerFactory.getLogger(ConveyorController.class);

    @Autowired
    private ConveyorService conveyorService;

    @Operation
    @PostMapping("/offers")
    public List<LoanOfferDTO> getOffers(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {
        logger.info(loanApplicationRequestDTO.toString());
        List<LoanOfferDTO> loanOfferDTOList = conveyorService.getOffers(loanApplicationRequestDTO);
        logger.info(loanOfferDTOList.toString());
        return loanOfferDTOList;
    }

    @Operation
    @PostMapping("/calculation")
    public CreditDTO calculation(@Valid @RequestBody ScoringDataDTO scoringDataDTO) throws ScoringException {
        logger.info(scoringDataDTO.toString());

        if (Scoring.isScoringPass(scoringDataDTO)) {
            CreditDTO creditDTO = conveyorService.getCreditCalculation(scoringDataDTO);
            logger.info(creditDTO.toString());
            return creditDTO;
        } else {
            throw  new ScoringException("Скоринг не пройден!");
        }
    }
}
