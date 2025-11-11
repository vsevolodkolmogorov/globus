package ru.globus.globusproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction extends BaseEntity {
    private BigDecimal amount;
    private String description;
    private BigDecimal exchangeRateUsed;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    public Transaction(BigDecimal amount, String description, BigDecimal exchangeRateUsed,
                       Account fromAccount, Account toAccount) {
        this.amount = amount;
        this.description = description;
        this.exchangeRateUsed = exchangeRateUsed;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public Transaction(BigDecimal amount, String description, BigDecimal exchangeRateUsed,
                       LocalDateTime createdAt, Account fromAccount, Account toAccount) {
        this.amount = amount;
        this.description = description;
        this.exchangeRateUsed = exchangeRateUsed;
        this.createdAt = createdAt;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + getId() + '\'' +
                ", amount=" + amount +
                ", description='" + description +
                ", exchangeRateUsed=" + exchangeRateUsed +
                ", createdAt=" + createdAt +
                ", fromAccount=" + fromAccount.getId() +
                ", toAccount=" + toAccount.getId() +
                '}';
    }
}
