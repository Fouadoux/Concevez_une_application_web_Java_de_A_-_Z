package com.paymybuddy.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "daily_limit",nullable = false)
    private long dailyLimit;

}
