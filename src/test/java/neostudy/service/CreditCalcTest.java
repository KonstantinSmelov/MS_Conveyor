package neostudy.service;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditCalcTest {

    @Test
    public void monthlyPaymentCalculationTest() {
        BigDecimal totalAmount = new BigDecimal(55000);
        BigDecimal rate = new BigDecimal(10);
        Integer term = 12;
        BigDecimal shouldBe = new BigDecimal("4835.37");

        Assert.assertEquals(shouldBe.toString(), CreditCalc.monthlyPaymentCalculation(totalAmount, rate, term).toString());
    }

    @Test
    public void interestPaymentCalculationTest() {
        BigDecimal remainingDebt = new BigDecimal("46948.68");
        BigDecimal rate = new BigDecimal(15);
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal shouldBe = new BigDecimal("586.86");

        Assert.assertEquals(shouldBe.toString(), CreditCalc.interestPaymentCalculation(remainingDebt, monthlyRate).toString());
    }

    @Test
    public void debtPaymentCalculationTest() {
        BigDecimal totalPayment = new BigDecimal("3676.32");
        BigDecimal interestPayment = new BigDecimal("586.86");
        BigDecimal shouldBe = new BigDecimal("3089.46");

        Assert.assertEquals(shouldBe.toString(), CreditCalc.debtPaymentCalculation(totalPayment, interestPayment).toString());
    }

    @Test
    public void remainDebtCalculationTest() {
        BigDecimal prevRemainDebt = new BigDecimal("43859.21");
        BigDecimal debtPayment = new BigDecimal("3128.08");
        BigDecimal shouldBe = new BigDecimal("40731.13");

        Assert.assertEquals(shouldBe.toString(), CreditCalc.remainDebtCalculation(prevRemainDebt, debtPayment).toString());
    }
}
