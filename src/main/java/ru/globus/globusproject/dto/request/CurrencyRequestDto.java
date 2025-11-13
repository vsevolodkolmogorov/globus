package ru.globus.globusproject.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRequestDto {
    @NotBlank
    @Size(min = 3, max = 3)
    private String charCode;
    @NotBlank
    @Size(min = 3, max = 3)
    private String numCode;
    @NotBlank
    private String name;
    @NotNull
    private Integer nominal;
    @NotNull
    private BigDecimal value;
}
