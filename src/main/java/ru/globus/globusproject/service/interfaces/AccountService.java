package ru.globus.globusproject.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.globus.globusproject.dto.request.AccountRequestDto;
import ru.globus.globusproject.dto.response.AccountResponseDto;

public interface AccountService {
    AccountResponseDto getById(Long Id);
    Page<AccountResponseDto> getAll(Pageable pageable);
    AccountResponseDto create(AccountRequestDto accountRequestDto);
    void delete(Long id);
}
