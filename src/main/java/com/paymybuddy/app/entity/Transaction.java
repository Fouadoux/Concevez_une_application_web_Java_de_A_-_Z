package com.paymybuddy.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@Table(name = "transaction")
public class Transaction {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "receiver_id",nullable = false)
    private User userReceiver;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "amount_with_fee", nullable = false)
    private long amountWithFee;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;


}
