package ru.globus.globusproject.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.globus.globusproject.dto.request.AccountRequestDto;
import ru.globus.globusproject.dto.response.AccountResponseDto;
import ru.globus.globusproject.model.Account;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "currency.id", target = "currencyId")
    AccountResponseDto toDto(Account account);

    List<AccountResponseDto> toDtoList(List<Account> accounts);

    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "currencyId", target = "currency.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Account toEntity(AccountRequestDto dto);
}
