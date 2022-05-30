package neostudy.service;

import neostudy.EmploymentStatus;
import neostudy.Gender;
import neostudy.MaritalStatus;
import neostudy.Position;
import neostudy.dto.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConveyorServiceImpl implements ConveyorService {

    long id = 0;

    @Override
    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        id++;
        List<LoanOfferDTO> offersList = new ArrayList<>();

        for (int x = 0; x < 4; x++) {
            if (x == 0) {
                offersList.add(appToOffer(loanApplicationRequestDTO, true, false));
            }
            if (x == 1) {
                offersList.add(appToOffer(loanApplicationRequestDTO, true, true));
            }
            if (x == 2) {
                offersList.add(appToOffer(loanApplicationRequestDTO, false, false));
            }
            if (x == 3) {
                offersList.add(appToOffer(loanApplicationRequestDTO, false, true));
            }
        }

//        Collections.sort(offersList, (LoanOfferDTO o1, LoanOfferDTO o2)-> o2.getMonthlyPayment().compareTo(o1.getMonthlyPayment()));
        return offersList;
    }

    private LoanOfferDTO appToOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        LoanOfferDTO offer = new LoanOfferDTO();
        BigDecimal rate = new BigDecimal(15); // базовая ставка

        offer.setApplicationId(id);
        offer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        offer.setTerm(loanApplicationRequestDTO.getTerm());
        offer.setIsInsuranceEnabled(isInsuranceEnabled);
        offer.setIsSalaryClient(isSalaryClient);

        offer.setTotalAmount(isInsuranceEnabled ?
                offer.getRequestedAmount().add(offer.getRequestedAmount().multiply(BigDecimal.valueOf(0.03))) :
                offer.getRequestedAmount());

        rate = rate.subtract(isInsuranceEnabled ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0));
        rate = rate.subtract(isSalaryClient ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0));
        offer.setRate(rate);

        offer.setMonthlyPayment(CreditCalc.monthlyPaymentCalculation(offer.getTotalAmount(), offer.getRate(), offer.getTerm()));

        return offer;
    }

    @Override
    public CreditDTO getCreditCalculation(ScoringDataDTO scoringDataDTO) {
        CreditDTO creditDTO = new CreditDTO();
        BigDecimal rate = new BigDecimal(15);

        rate = rate.subtract(scoringDataDTO.getIsInsuranceEnabled() ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getIsSalaryClient() ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.SELF_EMPLOYED ? BigDecimal.valueOf(-1) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER ? BigDecimal.valueOf(-3) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getEmployment().getPosition() == Position.MANAGER ? BigDecimal.valueOf(2) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getEmployment().getPosition() == Position.DIRECTOR ? BigDecimal.valueOf(4) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getMaritalStatus() == MaritalStatus.MARRIED ? BigDecimal.valueOf(3) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getMaritalStatus() == MaritalStatus.DIVORCED ? BigDecimal.valueOf(-1) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getDependentAmount() > 1 ? BigDecimal.valueOf(-1) : BigDecimal.valueOf(0));
        rate = rate.subtract(scoringDataDTO.getGender() == Gender.X ? BigDecimal.valueOf(-3) : BigDecimal.valueOf(0));
        rate = rate.subtract(checkGenderAndAgeForRateCorrection(scoringDataDTO));

        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
        creditDTO.setRate(rate);
        creditDTO.setMonthlyPayment(CreditCalc.monthlyPaymentCalculation(scoringDataDTO.getAmount(), rate, scoringDataDTO.getTerm()));
        creditDTO.setPsk(creditDTO.getMonthlyPayment().multiply(BigDecimal.valueOf(creditDTO.getTerm())));
        creditDTO.setPaymentSchedule(getPaymentScheduleList(creditDTO));

        return creditDTO;
    }

    private BigDecimal checkGenderAndAgeForRateCorrection(ScoringDataDTO scoringDataDTO) {
        BigDecimal rateCorrection = new BigDecimal(0);
        LocalDate today = LocalDate.now();
        LocalDate minForWomen = scoringDataDTO.getBirthdate().plus(35, ChronoUnit.YEARS);
        LocalDate maxForWomen = scoringDataDTO.getBirthdate().plus(60, ChronoUnit.YEARS);
        LocalDate minForMen = scoringDataDTO.getBirthdate().plus(30, ChronoUnit.YEARS);
        LocalDate maxForMen = scoringDataDTO.getBirthdate().plus(65, ChronoUnit.YEARS);

        if (scoringDataDTO.getGender() == Gender.FEMALE && minForWomen.isBefore(today) && maxForWomen.isAfter(today)) {
            rateCorrection = BigDecimal.valueOf(3);
        }
        if (scoringDataDTO.getGender() == Gender.MALE && minForMen.isBefore(today) && maxForMen.isAfter(today)) {
            rateCorrection = BigDecimal.valueOf(3);
        }
        return rateCorrection;
    }

    private List<PaymentScheduleElement> getPaymentScheduleList(CreditDTO creditDTO) {
        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();
        BigDecimal monthlyRate = creditDTO.getRate().divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        for (int x = 0; x < creditDTO.getTerm(); x++) {
            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();
            paymentScheduleElement.setNumber(x + 1);
            paymentScheduleElement.setDate(LocalDate.now().plus(x + 1, ChronoUnit.MONTHS));
            paymentScheduleElement.setTotalPayment(creditDTO.getMonthlyPayment());

            paymentScheduleElement.setInterestPayment(x < 1
                    ? CreditCalc.interestPaymentCalculation(creditDTO.getAmount(), monthlyRate)
                    : CreditCalc.interestPaymentCalculation(paymentScheduleElementList.get(x - 1).getRemainingDebt(), monthlyRate).setScale(2, RoundingMode.HALF_UP));

            paymentScheduleElement.setDebtPayment(CreditCalc.debtPaymentCalculation(creditDTO.getMonthlyPayment(), paymentScheduleElement.getInterestPayment()).setScale(2, RoundingMode.HALF_UP));

            paymentScheduleElement.setRemainingDebt(x == 0
                    ? CreditCalc.remainDebtCalculation(creditDTO.getAmount(), paymentScheduleElement.getDebtPayment())
                    : CreditCalc.remainDebtCalculation(paymentScheduleElementList.get(x - 1).getRemainingDebt(), paymentScheduleElement.getDebtPayment()));

            paymentScheduleElementList.add(paymentScheduleElement);

        }
        return paymentScheduleElementList;
    }
}
