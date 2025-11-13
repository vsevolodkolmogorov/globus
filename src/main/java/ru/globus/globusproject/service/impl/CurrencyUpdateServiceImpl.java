package ru.globus.globusproject.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.globus.globusproject.client.CbrClient;
import ru.globus.globusproject.dto.xml.ValCursDto;
import ru.globus.globusproject.model.Currency;
import ru.globus.globusproject.repository.CurrencyRepository;
import ru.globus.globusproject.service.interfaces.CurrencyUpdateService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyUpdateServiceImpl implements CurrencyUpdateService {

    private final CbrClient cbrClient;
    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional
    public void updateCurrencies() {
        ValCursDto response = cbrClient.getDailyRates();
        if (response == null || response.getValutes() == null) {
            log.warn("ЦБ не вернул данные");
            // TODO: EXCEPTION
            return;
        }

        LocalDate date = LocalDate.parse(
                response.getDate(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        );

        response.getValutes().forEach(valute -> {
            BigDecimal value = new BigDecimal(valute.getValue().replace(",", "."));
            currencyRepository.findCurrenciesByCharCode(valute.getCharCode())
                    .ifPresentOrElse(
                            existing -> {
                                existing.setValue(value);
                                existing.setLastUpdated(date);
                            },
                            () -> currencyRepository.save(new Currency(
                                    valute.getCharCode(),
                                    valute.getNumCode(),
                                    valute.getName(),
                                    valute.getNominal(),
                                    value,
                                    date
                            ))
                    );
        });

        log.info("Курсы валют обновлены на дату {}", date);
    }
}
