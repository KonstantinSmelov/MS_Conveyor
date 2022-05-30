package neostudy.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Getter
@Setter
@EqualsAndHashCode
public class PaymentScheduleElement {

    Integer number;
    LocalDate date;
    BigDecimal totalPayment;
    BigDecimal interestPayment;
    BigDecimal debtPayment;
    BigDecimal remainingDebt;

    @Override
    public String toString() {
        return "\nPaymentScheduleElement {" +
                "\nnumber=" + number +
                "\n date=" + date +
                "\n totalPayment=" + totalPayment +
                "\n interestPayment=" + interestPayment +
                "\n debtPayment=" + debtPayment +
                "\n remainingDebt=" + remainingDebt +
                '}';
    }
}
