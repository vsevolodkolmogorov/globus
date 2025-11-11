package ru.globus.globusproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDto {
    @NotBlank
    private String accountNumber;
    @NotNull
    @PositiveOrZero
    private BigDecimal balance;
    @NotNull
    private Long clientId;
    @NotNull
    private Long currencyId;
}
