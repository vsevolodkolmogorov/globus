package ru.globus.globusproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.globusproject.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
