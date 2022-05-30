package neostudy.service;

import neostudy.EmploymentStatus;
import neostudy.dto.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ConveyorServiceImplTest {

    @Test
    public void getOffersTest() {
        ConveyorServiceImpl conveyorService = new ConveyorServiceImpl();
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        int idx = 0;
        List<LoanOfferDTO> shouldBe = new ArrayList<>();
        LoanOfferDTO loanOfferDTO1 = new LoanOfferDTO();
        LoanOfferDTO loanOfferDTO2 = new LoanOfferDTO();
        LoanOfferDTO loanOfferDTO3 = new LoanOfferDTO();
        LoanOfferDTO loanOfferDTO4 = new LoanOfferDTO();

        loanOfferDTO1.setIsSalaryClient(false);
        loanOfferDTO1.setIsInsuranceEnabled(true);
        loanOfferDTO1.setRate(BigDecimal.valueOf(14));
        loanOfferDTO1.setTerm(12);
        loanOfferDTO1.setMonthlyPayment(BigDecimal.valueOf(4624.04));
        loanOfferDTO1.setTotalAmount(BigDecimal.valueOf(51500.00).setScale(2, RoundingMode.HALF_UP));
        loanOfferDTO1.setRequestedAmount(BigDecimal.valueOf(50000));
        loanOfferDTO1.setApplicationId(1L);
        shouldBe.add(loanOfferDTO1);

        loanOfferDTO2.setIsSalaryClient(true);
        loanOfferDTO2.setIsInsuranceEnabled(true);
        loanOfferDTO2.setRate(BigDecimal.valueOf(13));
        loanOfferDTO2.setTerm(12);
        loanOfferDTO2.setMonthlyPayment(BigDecimal.valueOf(4599.84));
        loanOfferDTO2.setTotalAmount(BigDecimal.valueOf(51500.00).setScale(2, RoundingMode.HALF_UP));
        loanOfferDTO2.setRequestedAmount(BigDecimal.valueOf(50000));
        loanOfferDTO2.setApplicationId(1L);
        shouldBe.add(loanOfferDTO2);

        loanOfferDTO3.setIsSalaryClient(false);
        loanOfferDTO3.setIsInsuranceEnabled(false);
        loanOfferDTO3.setRate(BigDecimal.valueOf(15));
        loanOfferDTO3.setTerm(12);
        loanOfferDTO3.setMonthlyPayment(BigDecimal.valueOf(4512.92));
        loanOfferDTO3.setTotalAmount(BigDecimal.valueOf(50000));
        loanOfferDTO3.setRequestedAmount(BigDecimal.valueOf(50000));
        loanOfferDTO3.setApplicationId(1L);
        shouldBe.add(loanOfferDTO3);

        loanOfferDTO4.setIsSalaryClient(true);
        loanOfferDTO4.setIsInsuranceEnabled(false);
        loanOfferDTO4.setRate(BigDecimal.valueOf(14));
        loanOfferDTO4.setTerm(12);
        loanOfferDTO4.setMonthlyPayment(BigDecimal.valueOf(4489.36));
        loanOfferDTO4.setTotalAmount(BigDecimal.valueOf(50000));
        loanOfferDTO4.setRequestedAmount(BigDecimal.valueOf(50000));
        loanOfferDTO4.setApplicationId(1L);
        shouldBe.add(loanOfferDTO4);

        loanApplicationRequestDTO.setTerm(12);
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(50000));

        for (LoanOfferDTO loanOfferDTO : conveyorService.getOffers(loanApplicationRequestDTO)) {
            Assert.assertEquals(shouldBe.get(idx++), loanOfferDTO);
        }

    }

    @Test
    public void getCreditCalculationTest() {
        ConveyorServiceImpl conveyorService = new ConveyorServiceImpl();
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        EmploymentDTO employmentDTO = new EmploymentDTO();
        CreditDTO shouldBe = new CreditDTO();
        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();
        PaymentScheduleElement paymentScheduleElement1 = new PaymentScheduleElement();
        PaymentScheduleElement paymentScheduleElement2 = new PaymentScheduleElement();

        paymentScheduleElement1.setNumber(1);
        paymentScheduleElement1.setDate(LocalDate.now().plus(1, ChronoUnit.MONTHS));
        paymentScheduleElement1.setTotalPayment(BigDecimal.valueOf(25406.98));
        paymentScheduleElement1.setInterestPayment(BigDecimal.valueOf(541.67));
        paymentScheduleElement1.setDebtPayment(BigDecimal.valueOf(24865.31));
        paymentScheduleElement1.setRemainingDebt(BigDecimal.valueOf(25134.69));

        paymentScheduleElement2.setNumber(2);
        paymentScheduleElement2.setDate(LocalDate.now().plus(2, ChronoUnit.MONTHS));
        paymentScheduleElement2.setTotalPayment(BigDecimal.valueOf(25406.98));
        paymentScheduleElement2.setInterestPayment(BigDecimal.valueOf(272.29));
        paymentScheduleElement2.setDebtPayment(BigDecimal.valueOf(25134.69));
        paymentScheduleElement2.setRemainingDebt(BigDecimal.valueOf(0));

        paymentScheduleElementList.add(paymentScheduleElement1);
        paymentScheduleElementList.add(paymentScheduleElement2);

        shouldBe.setAmount(BigDecimal.valueOf(50000));
        shouldBe.setTerm(2);
        shouldBe.setMonthlyPayment(BigDecimal.valueOf(25406.98));
        shouldBe.setRate(BigDecimal.valueOf(13));
        shouldBe.setPsk(BigDecimal.valueOf(50813.96));
        shouldBe.setIsInsuranceEnabled(true);
        shouldBe.setIsSalaryClient(true);
        shouldBe.setPaymentSchedule(paymentScheduleElementList);

        scoringDataDTO.setAmount(BigDecimal.valueOf(50000));
        scoringDataDTO.setDependentAmount(0);
        scoringDataDTO.setTerm(2);
        scoringDataDTO.setBirthdate(LocalDate.of(2000, 5, 28));
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.setIsSalaryClient(true);
        employmentDTO.setSalary(BigDecimal.valueOf(20000));
        employmentDTO.setWorkExperienceCurrent(10);
        employmentDTO.setWorkExperienceTotal(15);
        employmentDTO.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        scoringDataDTO.setEmployment(employmentDTO);

        Assert.assertEquals(shouldBe, conveyorService.getCreditCalculation(scoringDataDTO));



    }
}
