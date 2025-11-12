package ru.globus.globusproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.globus.globusproject.dto.request.TransactionRequestDto;
import ru.globus.globusproject.dto.response.TransactionResponseDto;
import ru.globus.globusproject.exception.DifferentCurrenciesException;
import ru.globus.globusproject.exception.TransactionNotFoundException;
import ru.globus.globusproject.model.Account;
import ru.globus.globusproject.model.Currency;
import ru.globus.globusproject.model.Transaction;
import ru.globus.globusproject.repository.TransactionRepository;
import ru.globus.globusproject.service.interfaces.internal.AccountInternalService;
import ru.globus.globusproject.utils.mapper.TransactionMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountInternalService accountService;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account fromAccount;
    private Account toAccount;
    private Currency rubCurrency;
    private TransactionRequestDto requestDto;
    private Transaction transaction;
    private TransactionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        rubCurrency = new Currency("RUB", "643", "Russian Ruble", 1, BigDecimal.ONE, null);

        fromAccount = new Account("1111", BigDecimal.valueOf(1000), null, rubCurrency);
        fromAccount.setId(1L);

        toAccount = new Account("2222", BigDecimal.valueOf(500), null, rubCurrency);
        toAccount.setId(2L);

        requestDto = new TransactionRequestDto(BigDecimal.valueOf(100), "description", 1L, 1L, 2L);
        transaction = new Transaction();
        transaction.setId(10L);

        responseDto = new TransactionResponseDto();
        responseDto.setId(10L);
        responseDto.setFromAccountId(fromAccount.getId());
        responseDto.setToAccountId(toAccount.getId());
    }

    @Test
    void transfer_ShouldCreateTransactionSuccessfully() {
        when(accountService.getEntityById(fromAccount.getId())).thenReturn(fromAccount);
        when(accountService.getEntityById(toAccount.getId())).thenReturn(toAccount);
        when(mapper.toEntity(requestDto)).thenReturn(transaction);
        when(repository.save(transaction)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(responseDto);

        TransactionResponseDto result = transactionService.transfer(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(accountService).withdraw(fromAccount, requestDto.getAmount());
        verify(accountService).deposit(toAccount, requestDto.getAmount());
        verify(repository).save(any(Transaction.class));
    }

    @Test
    void transfer_ShouldThrowException_WhenSameAccountIds() {
        requestDto = new TransactionRequestDto(BigDecimal.valueOf(100), "description", 1L, 1L, 1L);

        assertThatThrownBy(() -> transactionService.transfer(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be the same");

        verify(repository, never()).save(any());
    }

    @Test
    void transfer_ShouldThrowException_WhenDifferentCurrencies() {
        Currency usd = new Currency("USD", "840", "US Dollar", 1, BigDecimal.valueOf(90), null);
        toAccount = new Account("3333", BigDecimal.valueOf(300), null, usd);
        toAccount.setId(2L);

        when(accountService.getEntityById(fromAccount.getId())).thenReturn(fromAccount);
        when(accountService.getEntityById(toAccount.getId())).thenReturn(toAccount);

        assertThatThrownBy(() -> transactionService.transfer(requestDto))
                .isInstanceOf(DifferentCurrenciesException.class)
                .hasMessageContaining("different");

        verify(repository, never()).save(any());
    }

    @Test
    void getById_ShouldReturnTransactionResponse() {
        when(repository.findById(10L)).thenReturn(Optional.of(transaction));
        when(mapper.toDto(transaction)).thenReturn(responseDto);

        TransactionResponseDto result = transactionService.getById(10L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(repository).findById(10L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getById(99L))
                .isInstanceOf(TransactionNotFoundException.class);

        verify(repository).findById(99L);
    }

    @Test
    void getAll_ShouldReturnPageOfTransactions() {
        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        Page<TransactionResponseDto> responsePage = new PageImpl<>(List.of(responseDto));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(transaction)).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
        verify(repository).findAll(any(Pageable.class));
    }
}
