package neostudy.controller;

import neostudy.dto.CreditDTO;
import neostudy.dto.LoanApplicationRequestDTO;
import neostudy.dto.LoanOfferDTO;
import neostudy.dto.ScoringDataDTO;
import neostudy.exception.ScoringException;
import neostudy.service.ConveyorService;
import neostudy.service.Scoring;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class ConveyorControllerTest {

    @Autowired
    private ConveyorController conveyorController;

    @MockBean
    private ConveyorService conveyorService;

    @MockBean
    private Scoring scoring;

    private ScoringDataDTO fakeScoringDataDTO = null;
    private CreditDTO fakeCreditDTO = null;

    @Test
    public void getLoanOffers() {
        List<LoanOfferDTO> fakeLoanOfferDtoList = List.of(
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build());

        Mockito.when(conveyorService.getOffers(LoanApplicationRequestDTO.builder().build())).thenReturn(fakeLoanOfferDtoList);

        conveyorController.getLoanOffers(LoanApplicationRequestDTO.builder().build());
        Assert.assertEquals(3, (conveyorController.getLoanOffers(LoanApplicationRequestDTO.builder().build()).size()));
    }

    @Test
    void calculationWithScoringPass() throws ScoringException {
        fakeScoringDataDTO = ScoringDataDTO.builder().amount(BigDecimal.TEN).build();
        fakeCreditDTO = CreditDTO.builder().amount(BigDecimal.TEN).build();

        Mockito.when(conveyorService.getCreditCalculation(fakeScoringDataDTO)).thenReturn(fakeCreditDTO);

        Assert.assertEquals(fakeCreditDTO.getAmount(), (conveyorController.calculation(fakeScoringDataDTO)).getAmount());
    }

    @Test
    void calculationWithScoringFail() throws ScoringException {
        fakeScoringDataDTO = ScoringDataDTO.builder().amount(BigDecimal.TEN).build();
        fakeCreditDTO = CreditDTO.builder().amount(BigDecimal.TEN).build();

        Mockito.doThrow(ScoringException.class).when(scoring).checkForScoringErrors(fakeScoringDataDTO);
        Mockito.when(conveyorService.getCreditCalculation(fakeScoringDataDTO)).thenReturn(fakeCreditDTO);

        ScoringException e = Assert.assertThrows(ScoringException.class, () -> conveyorController.calculation(fakeScoringDataDTO));
        Assert.assertNotNull(e);
    }
}