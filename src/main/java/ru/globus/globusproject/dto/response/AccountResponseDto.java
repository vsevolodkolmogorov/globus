package ru.globus.globusproject.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Long clientId;
    private Long currencyId;
}
