package neostudy.service;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public class CreditCalc {

    public static BigDecimal monthlyPaymentCalculation(BigDecimal totalAmount, BigDecimal rate, Integer term) {

        //формулы брал отсюда
        //https://temabiz.com/finterminy/ap-formula-i-raschet-annuitetnogo-platezha.html

        //формула ежемесячной процентной ставки (i)
        // i = rate / 100 / 12;
        BigDecimal i = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        log.info("monthlyPaymentCalculation(): Ежемесячная процентная ставка i: {}", i);

        //общая формула расчёта месячного аннуитетного платежа
        //monthlyPayment = totalAmount * ( i + i / ((1+i)^term - 1) )
        BigDecimal monthlyPayment = totalAmount.multiply
                        (i.add
                                (i.divide
                                        (CreditCalc.pow(i.add(BigDecimal.valueOf(1)), term)
                                                .subtract(BigDecimal.valueOf(1)), MathContext.DECIMAL32)))
                .setScale(2, RoundingMode.HALF_UP);

        log.info("monthlyPaymentCalculation(): monthlyPayment: {}", monthlyPayment);

        return monthlyPayment;
    }

    private static BigDecimal pow(BigDecimal value, Integer degree) {
        BigDecimal result = BigDecimal.valueOf(1);
        for (int i = 1; i <= degree; i++) {
            result = result.multiply(value);
        }
//        log.info("pow(): Возведение {} в степень {} = {}", value, degree, result);
        return result;
    }

    //Проценты за кредит, выплачиваемые каждый месяц
    public static BigDecimal interestPaymentCalculation(BigDecimal remainingDebt, BigDecimal monthlyRate) {
        BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
        log.info("interestPaymentCalculation(): remainingDebt * monthlyRate = interestPayment: {} * {} = {}", remainingDebt, monthlyRate, interestPayment);
        return interestPayment;
    }

    //Погашение основного долга, выплачиваемое каждый месяц
    public static BigDecimal debtPaymentCalculation(BigDecimal totalPayment, BigDecimal interestPayment) {
        BigDecimal debtPayment = totalPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
        log.info("debtPayment(): totalPayment - interestPayment = debtPayment: {} - {} = {}", totalPayment, interestPayment, debtPayment);
        return debtPayment;
    }

    //Оставшийся долг на каждый месяц
    public static BigDecimal remainDebtCalculation(BigDecimal prevRemainDebt, BigDecimal debtPayment) {
        BigDecimal remainDebt = prevRemainDebt.subtract(debtPayment).compareTo(BigDecimal.valueOf(0.1)) < 0.1 ? BigDecimal.ZERO : prevRemainDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
        log.info("remainDebt(): prevRemainDebt - debtPayment = remainDebt: {} - {} = {}", prevRemainDebt, debtPayment, remainDebt);
        return remainDebt;
    }
}
