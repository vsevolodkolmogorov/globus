package ru.globus.globusproject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDto {
    @NotNull
    private Long clientId;
    @NotNull
    private Long currencyId;
}
