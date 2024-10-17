package com.paymybuddy.app.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions_fee")
public class TransactionFee {

    @Id
    @Column(name = "fee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fee;

    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    public long getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
