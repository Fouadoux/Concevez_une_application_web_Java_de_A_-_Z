package com.paymybuddy.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions_fee")
public class TransactionFee {

    @Id
    @Column(name = "fee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feeId;

    @Column(name = "percentage",nullable = false)
    private BigDecimal percentage;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;


}
