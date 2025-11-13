package ru.globus.globusproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate localDate = LocalDate.now();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    public Client(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Client(String firstName, String lastName, String email, LocalDate localDate, List<Account> accounts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.localDate = localDate;
        this.accounts = accounts != null ? accounts : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + getId() + '\'' +
                ", firstName='" + firstName +
                ", lastName='" + lastName +
                ", email='" + email  +
                ", localDate=" + localDate +
                ", accounts=" + accounts +
                '}';
    }
}
