package com.paymybuddy.app.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private int senderId;
    String senderName;
    private int receiverId;
    String receiverName;
    private BigDecimal amount;
    private BigDecimal amountWithFee;
    private String description;
    private LocalDateTime transactionDate;
}
