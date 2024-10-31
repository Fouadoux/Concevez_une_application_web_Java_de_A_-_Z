package com.paymybuddy.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {


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

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Cette méthode sera appelée juste avant de persister l'entité
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

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
    public void addUserRelation(UserRelation userRelation) {
        userRelations.add(userRelation);
        userRelation.setUser(this);
    }

    public void removeUserRelation(UserRelation userRelation) {
        userRelations.remove(userRelation);
        userRelation.setUser(this);
    }

}