package neostudy.service;

import neostudy.dto.CreditDTO;
import neostudy.dto.LoanApplicationRequestDTO;
import neostudy.dto.LoanOfferDTO;
import neostudy.dto.ScoringDataDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ConveyorService {

    List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    CreditDTO getCreditCalculation(ScoringDataDTO scoringDataDTO);
}
