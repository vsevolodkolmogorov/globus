package ru.globus.globusproject.utils.mapper;

import org.mapstruct.Mapper;
import ru.globus.globusproject.dto.response.CurrencyResponseDto;
import ru.globus.globusproject.model.Currency;


@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyResponseDto toDto(Currency currency);
}
