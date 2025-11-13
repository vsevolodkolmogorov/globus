package ru.globus.globusproject.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.globus.globusproject.dto.request.ClientRequestDto;
import ru.globus.globusproject.dto.response.ClientResponseDto;
import ru.globus.globusproject.model.Client;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface ClientMapper {
    ClientResponseDto toDto(Client client);
    List<ClientResponseDto> toDtoList(List<Client> clients);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "localDate", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    Client toEntity(ClientRequestDto dto);
}
