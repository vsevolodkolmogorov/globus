package ru.globus.globusproject.utils.mapper;

import org.mapstruct.Mapper;
import ru.globus.globusproject.dto.request.CurrencyRequestDto;
import ru.globus.globusproject.dto.response.CurrencyResponseDto;
import ru.globus.globusproject.model.Currency;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyResponseDto toDto(Currency currency);
    List<CurrencyResponseDto> toDtoList(List<Currency> currency);
    Currency toEntity(CurrencyRequestDto dto);
}
