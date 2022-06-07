package neostudy.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanApplicationRequestDTO {

    @Min(value = 10000L, message = "Кредит не может быть меньше 10000")
    private BigDecimal amount;

    @Min(value = 6, message = "Срок кредита не менее 6 месяцев")
    private Integer term;

    @NotBlank(message = "Не указано имя")
    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]{2,30}", message = "Имя не должно быть короче 2 и длиннее 30 символов и должно состоять из букв и цифр")
    private String firstName;

    @NotBlank(message = "Не указана фамилия")
    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]{2,30}", message = "Фамилия не должна быть короче 2 и длиннее 30 символов и должнасостоять из букв и цифр")
    private String lastName;

    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]{2,30}|", message = "Отчества или нет, или оно не должно быть короче 2 и длинее 30 символов и должно состоять из букв и цифр")
    private String middleName;

    @Email(message = "Некорректная почта")
    private String email;

    @Past(message = "Некорректная дата рождения")
    private LocalDate birthdate;

    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;

    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;
}
