package ru.globus.globusproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.globusproject.model.Currency;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findCurrenciesByCharCode(String charCode);
}
