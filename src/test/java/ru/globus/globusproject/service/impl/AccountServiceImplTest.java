package ru.globus.globusproject.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.globus.globusproject.dto.request.AccountRequestDto;
import ru.globus.globusproject.dto.response.AccountResponseDto;
import ru.globus.globusproject.exception.AccountAlreadyExistedException;
import ru.globus.globusproject.exception.AccountNotFoundException;
import ru.globus.globusproject.exception.InsufficientFundsException;
import ru.globus.globusproject.model.Account;
import ru.globus.globusproject.model.Client;
import ru.globus.globusproject.model.Currency;
import ru.globus.globusproject.repository.AccountRepository;
import ru.globus.globusproject.service.interfaces.internal.ClientInternalService;
import ru.globus.globusproject.service.interfaces.internal.CurrencyInternalService;
import ru.globus.globusproject.utils.mapper.AccountMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository repository;

    @Mock
    private ClientInternalService clientInternalService;

    @Mock
    private CurrencyInternalService currencyInternalService;

    @Mock
    private AccountMapper mapper;

    @InjectMocks
    private AccountServiceImpl service;

    private Account account;
    private AccountResponseDto accountResponseDto;
    private AccountRequestDto accountRequestDto;
    private final Client client = new Client("User1", "Test1", "test1@gmail.com");
    private final Currency currencyRub = new Currency("RUB", "036", "Русский рубль", 1, BigDecimal.valueOf(16, 2), LocalDate.now());
    private final Currency currencyUsd = new Currency("USD", "049", "Американский доллар", 1, BigDecimal.valueOf(23, 2), LocalDate.now());
    List<Account> Accounts = List.of(
            new Account("Number1", BigDecimal.ZERO, client, currencyRub),
            new Account("Number2", BigDecimal.ZERO, client, currencyUsd)
    );
    private final Page<Account> AccountPage = new PageImpl<>(Accounts, PageRequest.of(0, 10), Accounts.size());


    @BeforeEach
    void setUp() {
        account = new Account("Number", BigDecimal.ZERO, client, currencyRub);
        account.setId(1L);
        client.setId(1L);
        currencyRub.setId(1L);
        currencyUsd.setId(2L);

        accountRequestDto = AccountRequestDto.builder()
                .clientId(client.getId())
                .currencyId(currencyRub.getId())
                .build();

        accountResponseDto = AccountResponseDto.builder()
                .id(1L)
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .currencyId(account.getCurrency().getId())
                .clientId(account.getClient().getId())
                .build();
    }

    @Test
    void getById_shouldReturnAccountResponseDto_whenAccountExists() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.when(mapper.toDto(account)).thenReturn(accountResponseDto);

        AccountResponseDto result = service.getById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Number", result.getAccountNumber());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(mapper).toDto(account);
    }

    @Test
    void getById_shouldThrowException_whenAccountNotFound() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        AccountNotFoundException exception = Assertions.assertThrows(
                AccountNotFoundException.class,
                () -> service.getById(1L)
        );

        Assertions.assertEquals("Account with id 1 not founded!", exception.getMessage());
        Mockito.verify(repository).findById(1L);
    }

    @Test
    void getAll_shouldReturnPageOfAccountResponseDto_whenAccountsExists() {
        Mockito.when(repository.findAll(Mockito.any(Pageable.class))).thenReturn(AccountPage);

        Accounts.forEach(account ->
                Mockito.when(mapper.toDto(account))
                        .thenReturn(AccountResponseDto.builder()
                                .id(account.getId())
                                .accountNumber(account.getAccountNumber())
                                .balance(account.getBalance())
                                .currencyId(account.getCurrency().getId())
                                .clientId(account.getClient().getId())
                                .build())
        );

        Page<AccountResponseDto> resultPage = service.getAll(PageRequest.of(0, 10));

        Assertions.assertNotNull(resultPage);
        Assertions.assertEquals(Accounts.size(), resultPage.getTotalElements());
        Assertions.assertEquals(Accounts.size(), resultPage.getContent().size());

        Assertions.assertEquals("Number1", resultPage.getContent().get(0).getAccountNumber());

        Mockito.verify(repository).findAll(Mockito.any(Pageable.class));
        Accounts.forEach(account -> Mockito.verify(mapper).toDto(account));
    }

    @Test
    void getAll_shouldReturnPageOfAccountResponseDto_whenAccountsNotExists() {
        Page<Account> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Mockito.when(repository.findAll(Mockito.any(Pageable.class))).thenReturn(emptyPage);

        Page<AccountResponseDto> resultPage = service.getAll(PageRequest.of(0, 10));

        Assertions.assertNotNull(resultPage);
        Assertions.assertTrue(resultPage.getContent().isEmpty());
        Assertions.assertEquals(0, resultPage.getTotalElements());
        Assertions.assertEquals(0, resultPage.getTotalPages());

        Mockito.verify(repository).findAll(Mockito.any(Pageable.class));
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void created_shouldReturnResponseDto_whenRequestDtoCorrect() {
        Mockito.when(mapper.toEntity(Mockito.any(AccountRequestDto.class))).thenReturn(account);
        Mockito.when(repository.save(Mockito.any(Account.class))).thenReturn(account);
        Mockito.when(mapper.toDto(Mockito.any(Account.class))).thenReturn(accountResponseDto);

        AccountResponseDto result = service.create(accountRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Number", result.getAccountNumber());

        Mockito.verify(mapper).toEntity(accountRequestDto);
        Mockito.verify(repository).save(account);
        Mockito.verify(mapper).toDto(account);
    }

    @Test
    void created_shouldReturnResponseDto_whenAccountWithSuchCurrencyAlreadyExisted() {
        Mockito.when(clientInternalService.getEntityById(Mockito.any(Long.class))).thenReturn(client);
        Mockito.when(currencyInternalService.getEntityById(Mockito.any(Long.class))).thenReturn(currencyRub);
        Mockito.when(repository.findAccountByClientIdAndCurrencyId(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(account);

        AccountAlreadyExistedException exception = Assertions.assertThrows(
                AccountAlreadyExistedException.class,
                () -> service.create(accountRequestDto)
        );

        Assertions.assertEquals("Client with id " + client.getId() + " already have account with currencyId " + currencyRub.getId(), exception.getMessage());
        Mockito.verify(repository).findAccountByClientIdAndCurrencyId(1L, 1L);
        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void deleted_void_whenAccountExisted() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(account));
        Mockito.doNothing().when(repository).deleteById(Mockito.any(Long.class));

        service.delete(1L);

        Mockito.verify(repository).deleteById(1L);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void deleted_void_whenAccountNotExisted() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        AccountNotFoundException exception = Assertions.assertThrows(
                AccountNotFoundException.class,
                () -> service.delete(1L)
        );

        Assertions.assertEquals("Account with id " + 1 + " not founded!", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.anyLong());
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void withdraw_shouldDecreaseBalance_whenSufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(500);
        account.setBalance(BigDecimal.valueOf(1000));

        service.withdraw(account, amount);

        Assertions.assertEquals(BigDecimal.valueOf(500), account.getBalance());
        Mockito.verify(repository).save(account);
    }

    @Test
    void withdraw_shouldThrowException_whenInsufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(1500);
        account.setBalance(BigDecimal.valueOf(1000));

        InsufficientFundsException exception = Assertions.assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(account, amount)
        );

        Assertions.assertEquals("Insufficient funds in the account " + account.getId(), exception.getMessage());
        Mockito.verify(repository, Mockito.never()).save(account);
    }

    @Test
    void withdraw_shouldThrowException_whenAccountIsNull() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.withdraw(null, BigDecimal.valueOf(100))
        );
    }

    @Test
    void deposit_shouldIncreaseBalance() {
        BigDecimal amount = BigDecimal.valueOf(300);
        account.setBalance(BigDecimal.valueOf(1000));

        service.deposit(account, amount);

        Assertions.assertEquals(BigDecimal.valueOf(1300), account.getBalance());
        Mockito.verify(repository).save(account);
    }

    @Test
    void deposit_shouldThrowException_whenAccountIsNull() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.deposit(null, BigDecimal.valueOf(100))
        );
    }

}