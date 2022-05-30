package neostudy.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Getter
@Setter
@EqualsAndHashCode
public class LoanApplicationRequestDTO {

    @Min(value = 10000L, message = "Кредит не может быть меньше 10000")
    BigDecimal amount;

    @Min(value = 6, message = "Срок кредита не менее 6 месяцев")
    Integer term;

    @NotBlank(message = "Не указано имя")
    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]{2,30}", message = "Имя не должно быть короче 2 и длиннее 30 символов и должно состоять из букв и цифр")
    String firstName;

    @NotBlank(message = "Не указана фамилия")
    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]{2,30}", message = "Фамилия не должна быть короче 2 и длиннее 30 символов и должнасостоять из букв и цифр")
    String lastName;

    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]{2,30}|", message = "Отчества или нет, или оно не должно быть короче 2 и длинее 30 символов и должно состоять из букв и цифр")
    String middleName;

    @Email(message = "Некорректная почта")
    String email;

    @Past(message = "Некорректная дата рождения")
    LocalDate birthdate;

    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна содержать 4 цифры")
    String passportSeries;

    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен содержать 6 цифр")
    String passportNumber;

    @Override
    public String toString() {
        return "LoanApplicationRequestDTO{ " +
                " amount=" + amount +
                ", term=" + term +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                " }";
    }
}
