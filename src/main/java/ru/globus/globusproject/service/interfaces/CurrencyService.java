package ru.globus.globusproject.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.globus.globusproject.dto.response.CurrencyResponseDto;

public interface CurrencyService {
    CurrencyResponseDto getById(Long Id);
    CurrencyResponseDto getByCharCode(String charCode);
    Page<CurrencyResponseDto> getAll(Pageable pageable);
}
