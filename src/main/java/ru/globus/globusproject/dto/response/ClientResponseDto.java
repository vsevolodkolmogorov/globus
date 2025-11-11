package ru.globus.globusproject.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate localDate;
    private List<AccountResponseDto> accounts;
}
