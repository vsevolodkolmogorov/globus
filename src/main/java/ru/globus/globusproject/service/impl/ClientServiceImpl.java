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
import ru.globus.globusproject.dto.request.ClientRequestDto;
import ru.globus.globusproject.dto.response.ClientResponseDto;
import ru.globus.globusproject.exception.ClientAlreadyExistedException;
import ru.globus.globusproject.exception.ClientNotFoundException;
import ru.globus.globusproject.model.Client;
import ru.globus.globusproject.repository.ClientRepository;
import ru.globus.globusproject.service.interfaces.internal.ClientInternalService;
import ru.globus.globusproject.service.interfaces.ClientService;
import ru.globus.globusproject.utils.mapper.ClientMapper;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class ClientServiceImpl implements ClientService, ClientInternalService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public ClientResponseDto getById(@NotNull Long id) {
        Client client = findClientOrThrow(id);
        ClientResponseDto responseDto = mapper.toDto(client);
        log.info("Retrieved client response with id {}, email {} from repo",
                responseDto.getId(), responseDto.getEmail());
        return responseDto;
    }


    @Override
    public Client getEntityById(Long id) {
        Client client = findClientOrThrow(id);
        log.info("Retrieved client with id {}, email {} from repo",
                client.getId(), client.getEmail());
        return client;
    }

    @Override
    public @NonNull Page<ClientResponseDto> getAll(Pageable pageable) {
        Page<Client> clientPage = repository.findAll(pageable);
        Page<ClientResponseDto> responseDtoPage = clientPage.map(mapper::toDto);
        log.info("Retrieved page of clients with pages {}, elements {} from repo",
                responseDtoPage.getTotalPages(), responseDtoPage.getTotalElements());
        return responseDtoPage;
    }

    @Override
    public ClientResponseDto create(@Valid ClientRequestDto clientRequestDto) {
        validationClientExisted(clientRequestDto);
        Client mappedDto = mapper.toEntity(clientRequestDto);
        Client client = repository.save(mappedDto);
        ClientResponseDto mappedEntityResult = mapper.toDto(client);
        log.info("Created client with id {}, email {} to repo",
                mappedEntityResult.getId(), mappedEntityResult.getEmail());
        return mappedEntityResult;
    }

    @Override
    public void delete(@NotNull Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Client with id: {} not founded for delete!", id);
                    return new ClientNotFoundException(id);
                });
        repository.deleteById(id);
        log.info("Deleted client with id {}, email {} from repo",
                client.getId(), client.getEmail());
    }

    private void validationClientExisted(ClientRequestDto clientRequestDto) {
        Client client = repository.findClientByEmail(clientRequestDto.getEmail());
        if (client != null) {
            log.error("Client with email: {} already existed!", client.getEmail());
            throw new ClientAlreadyExistedException("Client with email: " + client.getEmail() + " already existed!");
        }
    }

    private Client findClientOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Client with id: {} not found!", id);
                    return new ClientNotFoundException(id);
                });
    }
}
