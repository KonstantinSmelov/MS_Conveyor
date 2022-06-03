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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class ConveyorControllerTest {

    @Autowired
    ConveyorController conveyorController;

    @MockBean
    private ConveyorService conveyorService;

    @MockBean
    private Scoring scoring;

    private List<LoanOfferDTO> fakeLoanOfferDtoList = null;
    private List<String> fakeErrorsList = new LinkedList<>();
    private ScoringDataDTO fakeScoringDataDTO = null;
    private CreditDTO fakeCreditDTO = null;

    @Test
    public void getLoanOffers() {
        fakeLoanOfferDtoList = List.of(
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build());

        Mockito.when(conveyorService.getOffers(LoanApplicationRequestDTO.builder().build())).thenReturn(fakeLoanOfferDtoList);

        conveyorController.getLoanOffers(LoanApplicationRequestDTO.builder().build());
        Assert.assertEquals(3, (conveyorController.getLoanOffers(LoanApplicationRequestDTO.builder().build()).size()));
    }

    @Test
    void calculationWithScoringPass() throws ScoringException {
        fakeErrorsList = new ArrayList<>();
        fakeScoringDataDTO = ScoringDataDTO.builder().amount(BigDecimal.TEN).build();
        fakeCreditDTO = CreditDTO.builder().amount(BigDecimal.TEN).build();

        Mockito.when(scoring.getScoringErrorsList(fakeScoringDataDTO)).thenReturn(fakeErrorsList);
        Mockito.when(conveyorService.getCreditCalculation(fakeScoringDataDTO)).thenReturn(fakeCreditDTO);

        Assert.assertEquals(fakeCreditDTO.getAmount(), (conveyorController.calculation(fakeScoringDataDTO)).getAmount());
    }

    @Test
    void calculationWithScoringFail() {
        fakeErrorsList = List.of("error1", "error2", "error3");
        fakeScoringDataDTO = ScoringDataDTO.builder().amount(BigDecimal.TEN).build();
        fakeCreditDTO = CreditDTO.builder().amount(BigDecimal.TEN).build();

        Mockito.when(scoring.getScoringErrorsList(fakeScoringDataDTO)).thenReturn(fakeErrorsList);
        Mockito.when(conveyorService.getCreditCalculation(fakeScoringDataDTO)).thenReturn(fakeCreditDTO);

        ScoringException e = Assert.assertThrows(ScoringException.class, () -> conveyorController.calculation(fakeScoringDataDTO));
        Assert.assertEquals(e.getErrorList(), fakeErrorsList);
    }
}