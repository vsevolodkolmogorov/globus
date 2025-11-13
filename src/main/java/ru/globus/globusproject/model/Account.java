package ru.globus.globusproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account extends BaseEntity {
    @Column(unique = true)
    private String accountNumber;
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    public Account(String accountNumber, BigDecimal balance, Client client, Currency currency) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.client = client;
        this.currency = currency;
    }

    public Account(Long id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + getId() + '\'' +
                ", accountNumber='" + accountNumber +
                ", balance=" + balance +
                ", clientId=" + client.getId() +
                ", currencyId=" + currency.getId() +
                '}';
    }
}
