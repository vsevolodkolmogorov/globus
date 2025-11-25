package ru.globus.globusproject.service.impl;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.globus.globusproject.dto.request.TransactionRequestDto;
import ru.globus.globusproject.dto.response.TransactionResponseDto;
import ru.globus.globusproject.exception.DifferentCurrenciesException;
import ru.globus.globusproject.exception.TransactionNotFoundException;
import ru.globus.globusproject.kafka.EventPublisher;
import ru.globus.globusproject.model.Account;
import ru.globus.globusproject.model.Transaction;
import ru.globus.globusproject.repository.TransactionRepository;
import ru.globus.globusproject.service.interfaces.TransactionService;
import ru.globus.globusproject.service.interfaces.internal.AccountInternalService;
import ru.globus.globusproject.utils.mapper.TransactionMapper;

import static ru.globus.globusproject.dto.kafka.KafkaTopics.TRANSACTION_EVENT;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final AccountInternalService accountService;
    private final EventPublisher eventPublisher;
    private final TransactionMapper mapper;

    @Override
    public TransactionResponseDto getById(@NotNull Long id) {
        Transaction transaction = findTransactionOrThrow(id);
        TransactionResponseDto responseDto = mapper.toDto(transaction);
        log.info("Retrieved transaction response with id {}, from AccountId {} to AccountId {}",
                responseDto.getId(), responseDto.getFromAccountId(), responseDto.getToAccountId());
        return responseDto;
    }

    @Override
    public @NonNull Page<TransactionResponseDto> getAll(Pageable pageable) {
        Page<Transaction> transactionPage = repository.findAll(pageable);
        Page<TransactionResponseDto> responseDtoPage = transactionPage.map(mapper::toDto);
        log.info("Retrieved page of transactions with pages {}, elements {} from repo",
                responseDtoPage.getTotalPages(), responseDtoPage.getTotalElements());
        return responseDtoPage;
    }

    @Override
    @Transactional
    public TransactionResponseDto transfer(@Valid TransactionRequestDto transactionRequestDto) {
        makeTransaction(transactionRequestDto);
        Transaction entity = mapper.toEntity(transactionRequestDto);
        Transaction transaction = repository.save(entity);
        TransactionResponseDto responseDto = mapper.toDto(transaction);
        eventPublisher.publish(TRANSACTION_EVENT.name, responseDto.getId(), responseDto);
        log.info("Created transaction response with id {}, from AccountId {} to AccountId {}",
                responseDto.getId(), responseDto.getFromAccountId(), responseDto.getToAccountId());
        return responseDto;
    }

    private void makeTransaction(@Valid TransactionRequestDto transactionRequestDto) {
        if (transactionRequestDto.getFromAccountId().equals(transactionRequestDto.getToAccountId())) {
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same");
        }

        Account from = accountService.getEntityById(transactionRequestDto.getFromAccountId());
        Account to = accountService.getEntityById(transactionRequestDto.getToAccountId());

        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new DifferentCurrenciesException("Currencies of from and to account is different");
        }

        accountService.withdraw(from, transactionRequestDto.getAmount());
        accountService.deposit(to, transactionRequestDto.getAmount());
    }

    private Transaction findTransactionOrThrow(@NotNull Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction with id: {} not found!", id);
                    return new TransactionNotFoundException(id);
                });
    }
}
