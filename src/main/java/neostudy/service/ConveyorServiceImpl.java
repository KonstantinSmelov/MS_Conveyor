package neostudy.service;

import lombok.extern.slf4j.Slf4j;
import neostudy.EmploymentStatus;
import neostudy.Gender;
import neostudy.MaritalStatus;
import neostudy.Position;
import neostudy.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConveyorServiceImpl implements ConveyorService {

    @Value("${custom.calculating.rate}")
    private BigDecimal hardCodedRate;

    @Override
    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        List<LoanOfferDTO> offersList = new ArrayList<>();
        offersList.add(appToOffer(loanApplicationRequestDTO, true, false));
        offersList.add(appToOffer(loanApplicationRequestDTO, true, true));
        offersList.add(appToOffer(loanApplicationRequestDTO, false, false));
        offersList.add(appToOffer(loanApplicationRequestDTO, false, true));
        log.info("getOffers():  offersList: {}", offersList);

        return offersList;
    }

    private LoanOfferDTO appToOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        BigDecimal rate = hardCodedRate;
        log.info("appToOffer(): Базовая Rate: {}", rate);
        BigDecimal totalAmount;

        if (isInsuranceEnabled) {
            totalAmount = loanApplicationRequestDTO.getAmount().add(loanApplicationRequestDTO.getAmount().multiply(BigDecimal.valueOf(0.03)));
        } else {
            totalAmount = loanApplicationRequestDTO.getAmount();
        }
        log.info("appToOffer(): isInsuranceEnabled {} -> TotalAmount: {}", isInsuranceEnabled, totalAmount.toString());

        if (isInsuranceEnabled) {
            rate = rate.subtract(BigDecimal.valueOf(1));
        }
        log.info("appToOffer(): isInsuranceEnabled {} -> Rate: {}", isInsuranceEnabled, rate);

        if (isSalaryClient) {
            rate = rate.subtract(BigDecimal.valueOf(1));
        }
        log.info("appToOffer(): isSalaryClient {} -> Rate: {}", isSalaryClient, rate);

        return LoanOfferDTO.builder()
                .applicationId(1L) //пока заглушка
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .term(loanApplicationRequestDTO.getTerm())
                .isInsuranceEnabled(isInsuranceEnabled)
                .totalAmount(totalAmount)
                .rate(rate)
                .monthlyPayment(CreditCalc.monthlyPaymentCalculation(totalAmount, rate, loanApplicationRequestDTO.getTerm()))
                .isSalaryClient(isSalaryClient).build();
    }

    @Override
    public CreditDTO getCreditCalculation(ScoringDataDTO scoringDataDTO) {

        BigDecimal rate = hardCodedRate;
        log.info("getCreditCalculation(): Базовая rate: {}", rate);
        BigDecimal amount = scoringDataDTO.getAmount();
        log.info("getCreditCalculation(): Базовый amount: {}", amount);

        if (scoringDataDTO.getIsSalaryClient()) {
            rate = rate.subtract(BigDecimal.valueOf(1));
            log.info("getCreditCalculation(): IsSalaryClient: {} -> rate: {}", scoringDataDTO.getIsSalaryClient(), rate);
        } else {
            log.info("getCreditCalculation(): IsSalaryClient: {} -> rate: {}", scoringDataDTO.getIsSalaryClient(), rate);
        }

        if (scoringDataDTO.getIsInsuranceEnabled()) {
            rate = rate.subtract(BigDecimal.valueOf(1));
            log.info("getCreditCalculation(): IsInsuranceEnabled: {} -> rate: {}", scoringDataDTO.getIsInsuranceEnabled(), rate);
            amount = amount.add(amount.multiply(BigDecimal.valueOf(0.03)));
            log.info("getCreditCalculation(): IsInsuranceEnabled: {} -> amount: {}", scoringDataDTO.getIsInsuranceEnabled(), amount);
        } else {
            log.info("getCreditCalculation(): IsInsuranceEnabled: {} -> rate: {}", scoringDataDTO.getIsInsuranceEnabled(), rate);
            log.info("getCreditCalculation(): IsInsuranceEnabled: {} -> amount: {}", scoringDataDTO.getIsInsuranceEnabled(), amount);
        }

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.SELF_EMPLOYED) {
            rate = rate.add(BigDecimal.valueOf(1));
            log.info("getCreditCalculation(): EmploymentStatus: {} -> rate: {}", scoringDataDTO.getEmployment().getEmploymentStatus(), rate);
        } else if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER) {
            rate = rate.add(BigDecimal.valueOf(3));
            log.info("getCreditCalculation(): EmploymentStatus: {} -> rate: {}", scoringDataDTO.getEmployment().getEmploymentStatus(), rate);
        }

        if (scoringDataDTO.getEmployment().getPosition() == Position.MANAGER) {
            rate = rate.subtract(BigDecimal.valueOf(2));
            log.info("getCreditCalculation(): Position: {} -> rate: {}", scoringDataDTO.getEmployment().getPosition(), rate);
        } else if (scoringDataDTO.getEmployment().getPosition() == Position.DIRECTOR) {
            rate = rate.subtract(BigDecimal.valueOf(4));
            log.info("getCreditCalculation(): Position: {} -> rate: {}", scoringDataDTO.getEmployment().getPosition(), rate);
        }

        if (scoringDataDTO.getMaritalStatus() == MaritalStatus.MARRIED) {
            rate = rate.subtract(BigDecimal.valueOf(3));
            log.info("getCreditCalculation(): MaritalStatus: {} -> rate: {}", scoringDataDTO.getMaritalStatus(), rate);
        } else if (scoringDataDTO.getMaritalStatus() == MaritalStatus.DIVORCED) {
            rate = rate.add(BigDecimal.valueOf(1));
            log.info("getCreditCalculation(): MaritalStatus: {} -> rate: {}", scoringDataDTO.getMaritalStatus(), rate);
        }

        if (scoringDataDTO.getDependentAmount() > 1) {
            rate = rate.add(BigDecimal.valueOf(1));
            log.info("getCreditCalculation(): DependentAmount: {} -> rate: {}", scoringDataDTO.getDependentAmount(), rate);
        } else {
            log.info("getCreditCalculation(): DependentAmount: {} -> rate: {}", scoringDataDTO.getDependentAmount(), rate);
        }

        if (scoringDataDTO.getGender() == Gender.X) {
            rate = rate.add(BigDecimal.valueOf(3));
            log.info("getCreditCalculation(): getGender: {} -> rate: {}", scoringDataDTO.getGender(), rate);
        } else {
            log.info("getCreditCalculation(): getGender: {} -> rate: {}", scoringDataDTO.getGender(), rate);
        }

        rate = rate.subtract(checkGenderAndAgeForRateCorrection(scoringDataDTO));
        log.info("getCreditCalculation(): checkGenderAndAgeForRateCorrection: {} -> rate: {}", checkGenderAndAgeForRateCorrection(scoringDataDTO), rate);

        BigDecimal monthlyPayment = CreditCalc.monthlyPaymentCalculation(amount, rate, scoringDataDTO.getTerm());

        return CreditDTO.builder()
                .amount(amount)
                .term(scoringDataDTO.getTerm())
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .rate(rate)
                .monthlyPayment(monthlyPayment)
                .psk(monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm())))
                .paymentSchedule(getPaymentScheduleList(rate, scoringDataDTO.getTerm(), monthlyPayment, amount))
                .build();
    }

    private BigDecimal checkGenderAndAgeForRateCorrection(ScoringDataDTO scoringDataDTO) {
        BigDecimal rateCorrection = new BigDecimal(0);
        LocalDate today = LocalDate.now();

        if (scoringDataDTO.getGender() == Gender.FEMALE
                && ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), today) > 35
                && ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), today) < 60) {
            rateCorrection = BigDecimal.valueOf(3);
        }

        if (scoringDataDTO.getGender() == Gender.MALE
                && ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), today) > 30
                && ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), today) < 55) {
            rateCorrection = BigDecimal.valueOf(3);
        }

        return rateCorrection;
    }

    private List<PaymentScheduleElement> getPaymentScheduleList(BigDecimal rate, Integer term, BigDecimal monthlyPayment, BigDecimal amount) {

        log.info("getPaymentScheduleList(): Входящие данные: rate = {}, term = {}, monthlyPayment = {}, amount = {}", rate, term, monthlyPayment, amount);
        List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal interestPayment;
        BigDecimal debtPayment;
        BigDecimal remainingDebt;

        for (int x = 0; x < term; x++) {

            if(x < 1) {
                interestPayment = CreditCalc.interestPaymentCalculation(amount, monthlyRate);
            } else {
                interestPayment = CreditCalc.interestPaymentCalculation(paymentScheduleElementList.get(x - 1).getRemainingDebt(), monthlyRate).setScale(2, RoundingMode.HALF_UP);
            }
            log.info("getPaymentScheduleList(): Месяц оплаты = {}, interestPayment = {}", x+1, interestPayment);

            debtPayment = CreditCalc.debtPaymentCalculation(monthlyPayment, interestPayment).setScale(2, RoundingMode.HALF_UP);
            log.info("getPaymentScheduleList(): Месяц оплаты = {}, debtPayment = {}", x+1, debtPayment);

            if(x == 0) {
                remainingDebt = CreditCalc.remainDebtCalculation(amount, debtPayment);
            } else {
                remainingDebt = CreditCalc.remainDebtCalculation(paymentScheduleElementList.get(x - 1).getRemainingDebt(), debtPayment);
            }
            log.info("getPaymentScheduleList(): Месяц оплаты = {}, remainingDebt = {}", x+1, remainingDebt);

            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                    .number(x+1)
                    .date(LocalDate.now().plus(x + 1, ChronoUnit.MONTHS))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt).build();

            paymentScheduleElementList.add(paymentScheduleElement);
        }
        return paymentScheduleElementList;
    }
}
