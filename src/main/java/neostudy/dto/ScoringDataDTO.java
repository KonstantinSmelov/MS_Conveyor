package neostudy.dto;

import lombok.Getter;
import lombok.Setter;
import neostudy.Gender;
import neostudy.MaritalStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Getter
@Setter
public class ScoringDataDTO {

    BigDecimal amount;
    Integer term;
    String firstName;
    String lastName;
    String middleName;
    Gender gender;
    LocalDate birthdate;
    String passportSeries;
    String passportNumber;
    LocalDate passportIssueDate;
    String passportIssueBranch;
    MaritalStatus maritalStatus;
    Integer dependentAmount;
    EmploymentDTO employment;
    String account;
    Boolean isInsuranceEnabled;
    Boolean isSalaryClient;

    @Override
    public String toString() {
        return "ScoringDataDTO{ " +
                "amount=" + amount +
                " term=" + term +
                " firstName='" + firstName + '\'' +
                " lastName='" + lastName + '\'' +
                " middleName='" + middleName + '\'' +
                " gender=" + gender +
                " birthdate=" + birthdate +
                " passportSeries='" + passportSeries + '\'' +
                " passportNumber='" + passportNumber + '\'' +
                " passportIssueDate=" + passportIssueDate +
                " passportIssueBranch='" + passportIssueBranch + '\'' +
                " maritalStatus=" + maritalStatus +
                " dependentAmount=" + dependentAmount +
                " account='" + account + '\'' +
                " isInsuranceEnabled=" + isInsuranceEnabled +
                " isSalaryClient=" + isSalaryClient +
                " employment=" + employment +
                "}";
    }
}
