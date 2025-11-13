package ru.globus.globusproject.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.globus.globusproject.dto.request.TransactionRequestDto;
import ru.globus.globusproject.dto.response.TransactionResponseDto;

public interface TransactionService {
    TransactionResponseDto getById(Long Id);
    Page<TransactionResponseDto> getAll(Pageable pageable);
    TransactionResponseDto transfer(TransactionRequestDto transactionRequestDto);
}
