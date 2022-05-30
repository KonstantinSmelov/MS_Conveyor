package neostudy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditCalc {

    private static final Logger logger = LoggerFactory.getLogger(CreditCalc.class);

    public static BigDecimal monthlyPaymentCalculation(BigDecimal totalAmount, BigDecimal rate, Integer term) {

        //ежемесячная процентная ставка (i)
        // i = rate / 100 / 12;
        BigDecimal i = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        logger.info("Ежемесячная процентная ставка: {}", i);

        // общая формула расчёта месячного аннуитетного платежа
        // monthlyPayment = totalAmount * ( i + i / ((1+i)^term - 1) )

        // A = (1+i)
        // monthlyPayment = totalAmount * ( i + i / ((A)^term - 1) )

        // B = (A)^term
        // monthlyPayment = totalAmount * ( i + i / (B - 1) )

        // C = i / (B - 1)
        // monthlyPayment = totalAmount * ( i + C )
        BigDecimal A = new BigDecimal(String.valueOf(i.add(BigDecimal.valueOf(1))));
        BigDecimal B = CreditCalc.pow(A, term);
        BigDecimal C = i.divide(B.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.HALF_UP);

        BigDecimal monthlyPayment = totalAmount.multiply(i.add(C)).setScale(2, RoundingMode.HALF_UP);
        logger.info("Ежемесячный платёж: {}", monthlyPayment);
        return monthlyPayment;
    }

    private static BigDecimal pow(BigDecimal value, Integer degree) {
        BigDecimal result = BigDecimal.valueOf(1);
        for (int i = 1; i <= degree; i++) {
            result = result.multiply(value);
        }

        logger.info("Возведение {} в степень {}: {}", value, degree, result);
        return result;
    }

    //Проценты за кредит, выплачиваемые каждый месяц
    public static BigDecimal interestPaymentCalculation(BigDecimal remainingDebt, BigDecimal monthlyRate) {
        BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
        logger.info("Оставшийся долг {} умножить на проценты {} равно банковским процентам {}", remainingDebt, monthlyRate, interestPayment);
        return interestPayment;
    }

    //Погашение основного долга, выплачиваемое каждый месяц
    public static BigDecimal debtPaymentCalculation(BigDecimal totalPayment, BigDecimal interestPayment) {
        BigDecimal debtPayment = totalPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
        logger.info("Общий платёж {} вычесть проценты банку {} равно основной долг {}", totalPayment, interestPayment, debtPayment);
        return debtPayment;
    }

    //Оставшийся долг на каждый месяц
    public static BigDecimal remainDebtCalculation(BigDecimal prevRemainDebt, BigDecimal debtPayment) {
        BigDecimal remainDebt = prevRemainDebt.subtract(debtPayment).compareTo(BigDecimal.valueOf(0.1)) < 0.1 ? BigDecimal.ZERO : prevRemainDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
        logger.info("Остаток долга за прошлый месяц {} минус погашение основного долга {} равно остатшейся задолженности {}", prevRemainDebt, debtPayment, remainDebt);
        return remainDebt;
    }


}
