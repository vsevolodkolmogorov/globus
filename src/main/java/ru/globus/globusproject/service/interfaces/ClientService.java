package ru.globus.globusproject.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.globus.globusproject.dto.request.ClientRequestDto;
import ru.globus.globusproject.dto.response.ClientResponseDto;

public interface ClientService {
    ClientResponseDto getById(Long Id);
    Page<ClientResponseDto> getAll(Pageable pageable);
    ClientResponseDto create(ClientRequestDto clientRequestDto);
    void delete(Long id);
}
