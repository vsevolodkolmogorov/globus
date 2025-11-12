package ru.globus.globusproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.globusproject.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByClientIdAndCurrencyId(Long clientId, Long currencyId);
}
