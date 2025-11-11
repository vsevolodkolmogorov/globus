package ru.globus.globusproject.utils.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.globus.globusproject.dto.request.TransactionRequestDto;
import ru.globus.globusproject.dto.response.TransactionResponseDto;
import ru.globus.globusproject.model.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "fromAccount.id", target = "fromAccountId")
    @Mapping(source = "toAccount.id", target = "toAccountId")
    TransactionResponseDto toDto(Transaction transaction);

    List<TransactionResponseDto> toDtoList(List<Transaction> transactions);

    @Mapping(target = "fromAccount", expression = "java(new Account(dto.getFromAccountId()))")
    @Mapping(target = "toAccount", expression = "java(new Account(dto.getToAccountId()))")
    Transaction toEntity(TransactionRequestDto dto);
}
