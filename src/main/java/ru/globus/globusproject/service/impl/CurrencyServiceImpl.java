package ru.globus.globusproject.service.impl;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.globus.globusproject.dto.response.CurrencyResponseDto;
import ru.globus.globusproject.exception.CurrencyNotFoundException;
import ru.globus.globusproject.model.Currency;
import ru.globus.globusproject.repository.CurrencyRepository;
import ru.globus.globusproject.service.interfaces.CurrencyService;
import ru.globus.globusproject.service.interfaces.internal.CurrencyInternalService;
import ru.globus.globusproject.utils.mapper.CurrencyMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService, CurrencyInternalService {

    private final CurrencyRepository repository;
    private final CurrencyMapper mapper;

    @Override
    public CurrencyResponseDto getById(@NotNull Long id) {
        Currency currency = findCurrencyOrThrow(id);
        CurrencyResponseDto responseDto = mapper.toDto(currency);
        log.info("Retrieved currency response by id {} from repo", responseDto.getId());
        return responseDto;
    }

    @Override
    public CurrencyResponseDto getByCharCode(@NotBlank String charCode) {
        Currency currency = findCurrencyOrThrow(charCode);
        CurrencyResponseDto responseDto = mapper.toDto(currency);
        log.info("Retrieved currency response by charCode {} from repo", responseDto.getCharCode());
        return responseDto;
    }

    @Override
    public Currency getEntityById(@NotNull Long id) {
        Currency currency = findCurrencyOrThrow(id);
        log.info("Retrieved currency entity by id {} from repo", currency.getId());
        return currency;
    }

    @Override
    public @NonNull Page<CurrencyResponseDto> getAll(Pageable pageable) {
        Page<Currency> currencyPage = repository.findAll(pageable);
        Page<CurrencyResponseDto> responseDtoPage = currencyPage.map(mapper::toDto);
        log.info("Retrieved page of currencies with pages {}, elements {} from repo",
                responseDtoPage.getTotalPages(), responseDtoPage.getTotalElements());
        return responseDtoPage;
    }

    private Currency findCurrencyOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Currency with id: {} not found!", id);
                    return new CurrencyNotFoundException(id);
                });
    }

    private Currency findCurrencyOrThrow(String charCode) {
        return repository.findCurrenciesByCharCode(charCode)
                .orElseThrow(() -> {
                    log.error("Currency with charCode: {} not found!", charCode);
                    return new CurrencyNotFoundException(charCode);
                });
    }
}
