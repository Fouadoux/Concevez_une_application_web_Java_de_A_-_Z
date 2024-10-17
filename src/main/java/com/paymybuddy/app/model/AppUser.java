package com.paymybuddy.app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int Id;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    private List<UserRelation> userRelations = new ArrayList<>();

    @OneToMany(mappedBy = "userSender"
    )
    private List<Transaction> senderTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "userReceiver"
    )
    private List<Transaction> receiverTransactions = new ArrayList<>();

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<UserRelation> getUserRelations() {
        return userRelations;
    }

    public void setUserRelations(List<UserRelation> userRelations) {
        this.userRelations = userRelations;
    }

    public List<Transaction> getSenderTransactions() {
        return senderTransactions;
    }

    public void setSenderTransactions(List<Transaction> senderTransaction) {
        this.senderTransactions = senderTransaction;
    }

    public List<Transaction> getReceiverTransactions() {
        return receiverTransactions;
    }

    public void setReceiverTransactions(List<Transaction> receiverTransaction) {
        this.receiverTransactions = receiverTransaction;
    }

    public void addSenderTransactions(Transaction transaction) {
        senderTransactions.add(transaction);
        transaction.setUserSender(this);
    }
    public void addReceiverTransactions(Transaction transaction) {
        receiverTransactions.add(transaction);
        transaction.setUserReceiver(this);
    }
    public void removeSenderTransactions(Transaction transaction) {
        senderTransactions.remove(transaction);
        transaction.setUserSender(null);
    }
    public void removeReceiverTransactions(Transaction transaction) {
        receiverTransactions.remove(transaction);
        transaction.setUserReceiver(null);
    }
}