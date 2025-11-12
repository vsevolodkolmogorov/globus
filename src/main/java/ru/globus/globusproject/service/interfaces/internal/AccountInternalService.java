package ru.globus.globusproject.service.interfaces.internal;

import ru.globus.globusproject.model.Account;

import java.math.BigDecimal;

public interface AccountInternalService {
    Account getEntityById(Long id);
    void withdraw(Account account, BigDecimal amount);
    void deposit(Account account, BigDecimal amount);
}
