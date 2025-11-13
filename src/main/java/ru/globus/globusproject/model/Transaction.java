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
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    public Transaction(BigDecimal amount, String description, Currency currency,
                       Account fromAccount, Account toAccount) {
        this.amount = amount;
        this.description = description;
        this.currency = currency;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public Transaction(BigDecimal amount, String description, Currency currency,
                       LocalDateTime createdAt, Account fromAccount, Account toAccount) {
        this.amount = amount;
        this.description = description;
        this.currency = currency;
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
                ", currencyId=" + currency.getId() +
                ", createdAt=" + createdAt +
                ", fromAccount=" + fromAccount.getId() +
                ", toAccount=" + toAccount.getId() +
                '}';
    }
}
