package com.paymybuddy.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "amount",nullable = false)
    @Min(value = 0, message = "The amount must be positive")
    private long amount;

    @Column(name = "bank_account",nullable = false)
    private String externalBankAccountNumber;

    @Column(name = "transfer_date",nullable = false)
    private LocalDateTime transferDate;

    @Column(name = "status")
    private boolean status;

}
