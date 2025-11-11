package ru.globus.globusproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.globusproject.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
