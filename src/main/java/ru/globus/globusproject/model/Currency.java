package ru.globus.globusproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Currency extends BaseEntity {

    @Column(unique = true)
    private String charCode;
    private String numCode;
    private String name;
    private Integer nominal;

    @Column(precision = 19, scale = 4)
    private BigDecimal value;

    private LocalDate lastUpdated;

    public Currency(String charCode, String numCode, String name, Integer nominal, BigDecimal value, LocalDate lastUpdated) {
        this.charCode = charCode;
        this.numCode = numCode;
        this.name = name;
        this.nominal = nominal;
        this.value = value;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id='" + getId() + '\'' +
                ", charCode='" + charCode +
                ", numCode='" + numCode +
                ", name='" + name  +
                ", nominal=" + nominal +
                ", value=" + value +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
