package ru.globus.globusproject.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.globus.globusproject.dto.request.TransactionRequestDto;
import ru.globus.globusproject.dto.response.TransactionResponseDto;
import ru.globus.globusproject.service.interfaces.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@Validated
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public TransactionResponseDto getById(@NotNull @PathVariable Long id) {
        log.info("Fetching transaction by id {}", id);
        return transactionService.getById(id);
    }

    @GetMapping
    public Page<TransactionResponseDto> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("Fetching all transactions by page {} and size {}",
                page, size);
        Pageable pageable = PageRequest.of(page, size);
        return transactionService.getAll(pageable);
    }

    @PostMapping
    public TransactionResponseDto create(@Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        log.info("Creating new transaction from accountId {} to accountId: {}",
                transactionRequestDto.getToAccountId(), transactionRequestDto.getFromAccountId());
        return transactionService.transfer(transactionRequestDto);
    }
}
