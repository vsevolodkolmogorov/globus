package ru.globus.globusproject.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto {
    @NotNull
    private BigDecimal amount;
    @Size(max = 255)
    private String description;
    @NotNull
    private BigDecimal exchangeRateUsed;
    @NotNull
    private Long fromAccountId;
    @NotNull
    private Long toAccountId;
}
