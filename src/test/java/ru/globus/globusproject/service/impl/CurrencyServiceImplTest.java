package ru.globus.globusproject.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.globus.globusproject.dto.response.CurrencyResponseDto;
import ru.globus.globusproject.exception.CurrencyNotFoundException;
import ru.globus.globusproject.model.Currency;
import ru.globus.globusproject.repository.CurrencyRepository;
import ru.globus.globusproject.utils.mapper.CurrencyMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository repository;

    @Mock
    private CurrencyMapper mapper;

    @InjectMocks
    private CurrencyServiceImpl service;

    private Currency currency;
    private CurrencyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        currency = new Currency(
                "USD",
                "840",
                "US Dollar",
                1,
                new BigDecimal("95.5000"),
                LocalDate.now()
        );
        currency.setId(1L);

        responseDto = new CurrencyResponseDto(
                1L,
                "USD",
                "840",
                "US Dollar",
                1,
                new BigDecimal("95.5000"),
                LocalDate.now()
        );
    }

    @Test
    void getById_ShouldReturnCurrencyResponseDto_WhenCurrencyExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(currency));
        when(mapper.toDto(currency)).thenReturn(responseDto);

        CurrencyResponseDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals("USD", result.getCharCode());
        verify(repository).findById(1L);
        verify(mapper).toDto(currency);
    }

    @Test
    void getById_ShouldThrowException_WhenCurrencyNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, () -> service.getById(1L));
        verify(repository).findById(1L);
        verifyNoInteractions(mapper);
    }

    @Test
    void getByCharCode_ShouldReturnCurrencyResponseDto_WhenCurrencyExists() {
        when(repository.findCurrenciesByCharCode("USD")).thenReturn(Optional.of(currency));
        when(mapper.toDto(currency)).thenReturn(responseDto);

        CurrencyResponseDto result = service.getByCharCode("USD");

        assertNotNull(result);
        assertEquals("USD", result.getCharCode());
        verify(repository).findCurrenciesByCharCode("USD");
        verify(mapper).toDto(currency);
    }

    @Test
    void getByCharCode_ShouldThrowException_WhenCurrencyNotFound() {
        when(repository.findCurrenciesByCharCode("USD")).thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, () -> service.getByCharCode("USD"));
        verify(repository).findCurrenciesByCharCode("USD");
        verifyNoInteractions(mapper);
    }

    @Test
    void getEntityById_ShouldReturnEntity_WhenCurrencyExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(currency));

        Currency result = service.getEntityById(1L);

        assertNotNull(result);
        assertEquals("USD", result.getCharCode());
        verify(repository).findById(1L);
    }

    @Test
    void getAll_ShouldReturnPagedResponse() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Currency> currencies = List.of(currency);
        Page<Currency> currencyPage = new PageImpl<>(currencies, pageable, 1);

        when(repository.findAll(pageable)).thenReturn(currencyPage);
        when(mapper.toDto(currency)).thenReturn(responseDto);

        Page<CurrencyResponseDto> result = service.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("USD", result.getContent().get(0).getCharCode());
        verify(repository).findAll(pageable);
        verify(mapper).toDto(currency);
    }
}
