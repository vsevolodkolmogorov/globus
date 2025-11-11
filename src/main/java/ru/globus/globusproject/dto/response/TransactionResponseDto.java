package ru.globus.globusproject.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private BigDecimal exchangeRateUsed;
    private LocalDateTime createdAt;
    private Long fromAccountId;
    private Long toAccountId;
}
