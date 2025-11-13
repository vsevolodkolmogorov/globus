package ru.globus.globusproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.globus.globusproject.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
