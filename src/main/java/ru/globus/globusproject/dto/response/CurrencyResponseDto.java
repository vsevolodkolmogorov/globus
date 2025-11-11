package ru.globus.globusproject.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyResponseDto {
    private Long id;
    private String charCode;
    private String numCode;
    private String name;
    private Integer nominal;
    private BigDecimal value;
    private LocalDate lastUpdated;
}
