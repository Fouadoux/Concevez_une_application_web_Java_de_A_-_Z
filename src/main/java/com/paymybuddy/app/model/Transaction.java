package com.paymybuddy.app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private AppUser userSender;

    @ManyToOne
    @JoinColumn(name = "receiver_id",nullable = false)
    private AppUser userReceiver;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppUser getUserSender() {
        return userSender;
    }

    public void setUserSender(AppUser appUserSender) {
        this.userSender = appUserSender;
    }

    public AppUser getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(AppUser appUserReceiver) {
        this.userReceiver = appUserReceiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
