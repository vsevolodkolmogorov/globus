package ru.globus.globusproject.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.globus.globusproject.dto.response.CurrencyResponseDto;
import ru.globus.globusproject.service.interfaces.CurrencyService;

@RestController
@RequestMapping("/api/currencies")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/{id}")
    public CurrencyResponseDto getById(@NotNull @PathVariable Long id) {
        log.info("Fetching currency by id {}", id);
        return currencyService.getById(id);
    }

    @GetMapping
    public CurrencyResponseDto getById(@NotNull @RequestParam String charCode) {
        log.info("Fetching currency by charCode {}", charCode);
        return currencyService.getByCharCode(charCode);
    }

    @GetMapping
    public Page<CurrencyResponseDto> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("Fetching all currencies by page {} and size {}",
                page, size);
        Pageable pageable = PageRequest.of(page, size);
        return currencyService.getAll(pageable);
    }
}
