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
import ru.globus.globusproject.dto.request.ClientRequestDto;
import ru.globus.globusproject.dto.response.ClientResponseDto;
import ru.globus.globusproject.service.interfaces.ClientService;

@RestController
@RequestMapping("/api/accounts")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ClientResponseDto getById(@NotNull @PathVariable Long id) {
        log.info("Fetching account by id {}", id);
        return clientService.getById(id);
    }

    @GetMapping
    public Page<ClientResponseDto> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        log.info("Fetching all accounts by page {} and size {}",
                page, size);
        Pageable pageable = PageRequest.of(page, size);
        return clientService.getAll(pageable);
    }

    @PostMapping
    public ClientResponseDto create(@Valid @RequestBody ClientRequestDto clientRequestDto) {
        log.info("Creating new account with email {}", clientRequestDto.getEmail());
        return clientService.create(clientRequestDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@NotNull @PathVariable Long id) {
        log.info("Deleting account with id {}", id);
        clientService.delete(id);
    }
}
