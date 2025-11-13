package ru.globus.globusproject.service.impl;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.globus.globusproject.dto.request.AccountRequestDto;
import ru.globus.globusproject.dto.response.AccountResponseDto;
import ru.globus.globusproject.exception.AccountAlreadyExistedException;
import ru.globus.globusproject.exception.AccountNotFoundException;
import ru.globus.globusproject.exception.InsufficientFundsException;
import ru.globus.globusproject.model.Account;
import ru.globus.globusproject.repository.AccountRepository;
import ru.globus.globusproject.service.interfaces.internal.AccountInternalService;
import ru.globus.globusproject.service.interfaces.AccountService;
import ru.globus.globusproject.service.interfaces.internal.ClientInternalService;
import ru.globus.globusproject.service.interfaces.internal.CurrencyInternalService;
import ru.globus.globusproject.utils.mapper.AccountMapper;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class AccountServiceImpl implements AccountService, AccountInternalService {

    private final AccountRepository repository;
    private final ClientInternalService clientService;
    private final CurrencyInternalService currencyService;
    private final AccountMapper mapper;

    @Override
    public AccountResponseDto getById(@NotNull Long id) {
        Account Account = findAccountOrThrow(id);
        AccountResponseDto responseDto = mapper.toDto(Account);
        log.info("Retrieved response account with id {}, number {} from repo",
                responseDto.getId(), responseDto.getAccountNumber());
        return responseDto;
    }

    @Override
    public @NonNull Page<AccountResponseDto> getAll(Pageable pageable) {
        Page<Account> AccountPage = repository.findAll(pageable);
        Page<AccountResponseDto> responseDtoPage = AccountPage.map(mapper::toDto);
        log.info("Retrieved page of accounts with pages {}, elements {} from repo",
                responseDtoPage.getTotalPages(), responseDtoPage.getTotalElements());
        return responseDtoPage;
    }

    @Override
    public AccountResponseDto create(@Valid AccountRequestDto accountRequestDto) {
        accountRequestDtoValidation(accountRequestDto);
        Account mappedDto = mapper.toEntity(accountRequestDto);
        mappedDto.setAccountNumber(accountNumberGenerator());
        mappedDto.setBalance(BigDecimal.ZERO);
        log.info("Prepared new account from dto with number {}, balance {} to repo",
                mappedDto.getAccountNumber(), mappedDto.getBalance());
        Account Account = repository.save(mappedDto);
        AccountResponseDto mappedEntityResult = mapper.toDto(Account);
        log.info("Created account with id {}, number {} to repo",
                mappedEntityResult.getId(), mappedEntityResult.getAccountNumber());
        return mappedEntityResult;
    }

    @Override
    public void delete(@NotNull Long id) {
        Account Account = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("account with id: {} not founded for delete!", id);
                    return new AccountNotFoundException(id);
                });
        repository.deleteById(id);
        log.info("Deleted account with id {}, number {} from repo",
                Account.getId(), Account.getAccountNumber());
    }

    @Override
    public Account getEntityById(Long id) {
        Account account = findAccountOrThrow(id);
        log.info("Retrieved entity account with id {}, number {} from repo",
                account.getId(), account.getAccountNumber());
        return account;
    }

    @Override
    public void withdraw(Account account, @NotNull BigDecimal amount) {
        Objects.requireNonNull(account, "Account must not be null");
        changeBalance(account, amount, false);
    }

    @Override
    public void deposit(Account account, @NotNull BigDecimal amount) {
        Objects.requireNonNull(account, "Account must not be null");
        changeBalance(account, amount, true);
    }

    private String accountNumberGenerator() {
        return String.format("%020d", new Random().nextLong(1_000_000_000L));
    }

    private Account findAccountOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account with id: {} not found!", id);
                    return new AccountNotFoundException(id);
                });
    }

    private void accountRequestDtoValidation(AccountRequestDto accountRequestDto) {
        clientService.getEntityById(accountRequestDto.getClientId());
        currencyService.getEntityById(accountRequestDto.getClientId());
        Account account = repository.findAccountByClientIdAndCurrencyId(accountRequestDto.getClientId(), accountRequestDto.getCurrencyId());
        if (account != null) {
            log.error("Client with id {} already have account with currencyId {}", accountRequestDto.getClientId(), accountRequestDto.getCurrencyId());
            throw new AccountAlreadyExistedException("Client with id " + accountRequestDto.getClientId() + " already have account with currencyId " + accountRequestDto.getCurrencyId());
        }
    }

    private void accountBalanceValidation(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds in the account {}", account.getId());
            throw new InsufficientFundsException(account.getId());
        }
    }

    private void changeBalance(Account account, BigDecimal amount, boolean isDeposit) {
        if (!isDeposit) {
            accountBalanceValidation(account, amount);
        }

        BigDecimal newBalance = isDeposit
                ? account.getBalance().add(amount)
                : account.getBalance().subtract(amount);

        account.setBalance(newBalance);
        repository.save(account);
        log.info("{} amount {} for account id {}, new balance {}",
                isDeposit ? "Deposit" : "Withdraw", amount, account.getId(), newBalance);
    }
}
