package ru.globus.globusproject.utils.mapper;

import org.mapstruct.Mapper;
import ru.globus.globusproject.dto.request.ClientRequestDto;
import ru.globus.globusproject.dto.response.ClientResponseDto;
import ru.globus.globusproject.model.Client;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface ClientMapper {
    ClientResponseDto toDto(Client client);
    List<ClientResponseDto> toDtoList(List<Client> clients);
    Client toEntity(ClientRequestDto dto);
}
