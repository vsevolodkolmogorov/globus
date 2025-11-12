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
import ru.globus.globusproject.dto.request.ClientRequestDto;
import ru.globus.globusproject.dto.response.ClientResponseDto;
import ru.globus.globusproject.exception.ClientAlreadyExistedException;
import ru.globus.globusproject.exception.ClientNotFoundException;
import ru.globus.globusproject.model.Client;
import ru.globus.globusproject.repository.ClientRepository;
import ru.globus.globusproject.utils.mapper.ClientMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {
    @Mock
    private ClientRepository repository;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientServiceImpl service;

    private Client client;
    private ClientResponseDto clientResponseDto;
    private ClientRequestDto clientRequestDto;
    List<Client> clients = List.of(
            new Client("User1", "Test1", "test1@gmail.com"),
            new Client("User2", "Test2", "test2@gmail.com")
    );
    private final Page<Client> clientPage = new PageImpl<>(clients, PageRequest.of(0, 10), clients.size());


    @BeforeEach
    void setUp() {
        client = new Client("user", "test", "test@gmail.com");
        client.setId(1L);

        clientRequestDto = ClientRequestDto.builder()
                .email("test@gmail.com")
                .firstName("user")
                .lastName("test")
                .build();

        clientResponseDto = ClientResponseDto.builder()
                .id(1L)
                .email("test@gmail.com")
                .firstName("user")
                .lastName("test")
                .accounts(null)
                .localDate(LocalDate.now())
                .build();
    }

    @Test
    void getById_shouldReturnClientResponseDto_whenClientExists() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(mapper.toDto(client)).thenReturn(clientResponseDto);

        ClientResponseDto result = service.getById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("test@gmail.com", result.getEmail());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(mapper).toDto(client);
    }

    @Test
    void getById_shouldThrowException_whenClientNotFound() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        ClientNotFoundException exception = Assertions.assertThrows(
                ClientNotFoundException.class,
                () -> service.getById(1L)
        );

        Assertions.assertEquals("Client with id 1 not founded!", exception.getMessage());
        Mockito.verify(repository).findById(1L);
    }

    @Test
    void getAll_shouldReturnPageOfClientResponseDto_whenClientsExists() {
        Mockito.when(repository.findAll(Mockito.any(Pageable.class))).thenReturn(clientPage);

        clients.forEach(client ->
                Mockito.when(mapper.toDto(client))
                        .thenReturn(ClientResponseDto.builder()
                                .id(client.getId())
                                .email(client.getEmail())
                                .firstName(client.getFirstName())
                                .lastName(client.getLastName())
                                .build())
        );

        Page<ClientResponseDto> resultPage = service.getAll(PageRequest.of(0, 10));

        Assertions.assertNotNull(resultPage);
        Assertions.assertEquals(clients.size(), resultPage.getTotalElements());
        Assertions.assertEquals(clients.size(), resultPage.getContent().size());

        Assertions.assertEquals("test1@gmail.com", resultPage.getContent().get(0).getEmail());

        Mockito.verify(repository).findAll(Mockito.any(Pageable.class));
        clients.forEach(client -> Mockito.verify(mapper).toDto(client));
    }

    @Test
    void getAll_shouldReturnPageOfClientResponseDto_whenClientsNotExists() {
        Page<Client> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Mockito.when(repository.findAll(Mockito.any(Pageable.class))).thenReturn(emptyPage);

        Page<ClientResponseDto> resultPage = service.getAll(PageRequest.of(0, 10));

        Assertions.assertNotNull(resultPage);
        Assertions.assertTrue(resultPage.getContent().isEmpty());
        Assertions.assertEquals(0, resultPage.getTotalElements());
        Assertions.assertEquals(0, resultPage.getTotalPages());

        Mockito.verify(repository).findAll(Mockito.any(Pageable.class));
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void created_shouldReturnResponseDto_whenRequestDtoCorrect() {
        Mockito.when(mapper.toEntity(Mockito.any(ClientRequestDto.class))).thenReturn(client);
        Mockito.when(repository.save(Mockito.any(Client.class))).thenReturn(client);
        Mockito.when(mapper.toDto(Mockito.any(Client.class))).thenReturn(clientResponseDto);

        ClientResponseDto result = service.create(clientRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("test@gmail.com", result.getEmail());

        Mockito.verify(mapper).toEntity(clientRequestDto);
        Mockito.verify(repository).save(client);
        Mockito.verify(mapper).toDto(client);
    }

    @Test
    void created_shouldReturnResponseDto_whenClientAlreadyExisted() {
        Mockito.when(repository.findClientByEmail(Mockito.any(String.class))).thenReturn(client);

        ClientAlreadyExistedException exception = Assertions.assertThrows(
                ClientAlreadyExistedException.class,
                () -> service.create(clientRequestDto)
        );

        Assertions.assertEquals("Client with email: test@gmail.com already existed!", exception.getMessage());
        Mockito.verify(repository).findClientByEmail("test@gmail.com");
        Mockito.verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void deleted_void_whenClientExisted() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.doNothing().when(repository).deleteById(Mockito.any(Long.class));

        service.delete(1L);

        Mockito.verify(repository).deleteById(1L);
        Mockito.verifyNoInteractions(mapper);
    }

    @Test
    void deleted_void_whenClientNotExisted() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        ClientNotFoundException exception = Assertions.assertThrows(
                ClientNotFoundException.class,
                () -> service.delete(1L)
        );

        Assertions.assertEquals("Client with id " + 1 + " not founded!", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.anyLong());
        Mockito.verifyNoInteractions(mapper);
    }
}