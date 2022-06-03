package neostudy.service;

import neostudy.EmploymentStatus;
import neostudy.MaritalStatus;
import neostudy.dto.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ConveyorServiceImplTest {

    @InjectMocks
    private ConveyorServiceImpl conveyorService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(conveyorService, "hardCodedRate", BigDecimal.valueOf(15));
    }

    @Test
    public void getOffersTest() {
        LoanApplicationRequestDTO loanApplicationRequestDTO;
        LoanOfferDTO loanOfferDTO1;
        LoanOfferDTO loanOfferDTO2;
        LoanOfferDTO loanOfferDTO3;
        LoanOfferDTO loanOfferDTO4;
        int idx = 0;
        List<LoanOfferDTO> shouldBe = new ArrayList<>();

        loanOfferDTO1 = LoanOfferDTO.builder().
                isSalaryClient(false).
                isInsuranceEnabled(true).
                rate(BigDecimal.valueOf(14)).
                term(12).
                monthlyPayment(BigDecimal.valueOf(4624.04)).
                totalAmount(BigDecimal.valueOf(51500).setScale(2, RoundingMode.HALF_UP)).
                requestedAmount(BigDecimal.valueOf(50000)).
                applicationId(1L).build();
        shouldBe.add(loanOfferDTO1);

        loanOfferDTO2 = LoanOfferDTO.builder().
                isSalaryClient(true).
                isInsuranceEnabled(true).
                rate(BigDecimal.valueOf(13)).
                term(12).
                monthlyPayment(BigDecimal.valueOf(4599.84)).
                totalAmount(BigDecimal.valueOf(51500).setScale(2, RoundingMode.HALF_UP)).
                requestedAmount(BigDecimal.valueOf(50000)).
                applicationId(1L).build();
        shouldBe.add(loanOfferDTO2);

        loanOfferDTO3 = LoanOfferDTO.builder().
                isSalaryClient(false).
                isInsuranceEnabled(false).
                rate(BigDecimal.valueOf(15)).
                term(12).
                monthlyPayment(BigDecimal.valueOf(4512.92)).
                totalAmount(BigDecimal.valueOf(50000)).
                requestedAmount(BigDecimal.valueOf(50000)).
                applicationId(1L).build();
        shouldBe.add(loanOfferDTO3);

        loanOfferDTO4 = LoanOfferDTO.builder().
                isSalaryClient(true).
                isInsuranceEnabled(false).
                rate(BigDecimal.valueOf(14)).
                term(12).
                monthlyPayment(BigDecimal.valueOf(4489.36)).
                totalAmount(BigDecimal.valueOf(50000)).
                requestedAmount(BigDecimal.valueOf(50000)).
                applicationId(1L).build();
        shouldBe.add(loanOfferDTO4);

        loanApplicationRequestDTO = LoanApplicationRequestDTO.builder().
                term(12).
                amount(BigDecimal.valueOf(50000)).build();

        for (LoanOfferDTO loanOfferDTO : conveyorService.getOffers(loanApplicationRequestDTO)) {
            Assert.assertEquals(shouldBe.get(idx++), loanOfferDTO);
        }
    }

    @Test
    public void getCreditCalculationTest() {
        ScoringDataDTO scoringDataDTO;
        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();
        PaymentScheduleElement paymentScheduleElement1;
        PaymentScheduleElement paymentScheduleElement2;

        paymentScheduleElement1 = PaymentScheduleElement.builder().
                number(1).
                date(LocalDate.now().plus(1, ChronoUnit.MONTHS)).
                totalPayment(BigDecimal.valueOf(25438.34)).
                interestPayment(BigDecimal.valueOf(583.33)).
                debtPayment(BigDecimal.valueOf(24855.01)).
                remainingDebt(BigDecimal.valueOf(25144.99)).build();

        paymentScheduleElement2 = PaymentScheduleElement.builder().
                number(2).
                date(LocalDate.now().plus(2, ChronoUnit.MONTHS)).
                totalPayment(BigDecimal.valueOf(25438.34)).
                interestPayment(BigDecimal.valueOf(293.36)).
                debtPayment(BigDecimal.valueOf(25144.98)).
                remainingDebt(BigDecimal.valueOf(0)).build();

        paymentScheduleElementList.add(paymentScheduleElement1);
        paymentScheduleElementList.add(paymentScheduleElement2);

        CreditDTO shouldBe = CreditDTO.builder().
                amount(BigDecimal.valueOf(50000)).
                term(2).
                monthlyPayment(BigDecimal.valueOf(25438.34)).
                rate(BigDecimal.valueOf(14)).
                psk(BigDecimal.valueOf(50876.68)).
                isInsuranceEnabled(false).
                isSalaryClient(true).
                paymentSchedule(paymentScheduleElementList).build();

        scoringDataDTO = ScoringDataDTO.builder().
                amount(BigDecimal.valueOf(50000)).
                dependentAmount(0).
                term(2).
                birthdate(LocalDate.of(2000, 5, 28)).
                isInsuranceEnabled(false).
                isSalaryClient(true).
                maritalStatus(MaritalStatus.MARRIED).build();

        EmploymentDTO employmentDTO = EmploymentDTO.builder().
                salary(BigDecimal.valueOf(20000)).
                workExperienceCurrent(15).
                workExperienceTotal(15).
                employmentStatus(EmploymentStatus.BUSINESS_OWNER).build();

        scoringDataDTO.setEmployment(employmentDTO);

        Assert.assertEquals(shouldBe, conveyorService.getCreditCalculation(scoringDataDTO));
    }
}
