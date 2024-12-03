package com.paymybuddy.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="monetization")
@Getter
@Setter
public class Monetization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;

    @OneToOne
    @JoinColumn( name = "transaction_id", nullable = false,unique = true)
    private Transaction transaction;

    @Column(name="result", nullable = false)
    private long result;
}
