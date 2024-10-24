package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.repository.TransactionFeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionFeeService {

    private final TransactionFeeRepository transactionFeeRepository;

    public TransactionFeeService(TransactionFeeRepository transactionFeeRepository) {
        this.transactionFeeRepository = transactionFeeRepository;
    }

    public ResponseEntity<?> createTransactionFee(TransactionFee fee) {
        fee.setEffectiveDate(LocalDateTime.now());
        transactionFeeRepository.save(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction fee created successfully");

    }

    public TransactionFee getActiveTransactionFee() {
        return transactionFeeRepository.findTopByOrderByEffectiveDateDesc()
                .orElseThrow(() -> new IllegalArgumentException("No transaction fee found"));
    }

    public ResponseEntity<?> updateTransactionFeePercentage(int id, BigDecimal bigDecimal) {
        return transactionFeeRepository.findById(id).map(fee -> {
            fee.setPercentage(bigDecimal);
            transactionFeeRepository.save(fee);
            return ResponseEntity.ok("Transaction fee update successfully");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction fee not found"));
    }

    public ResponseEntity<?> deleteTransactionFee(int id){
        return transactionFeeRepository.findById(id).map(fee -> {
            transactionFeeRepository.delete(fee);
            return ResponseEntity.ok("Transaction fee delete successfully");
        }).orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction fee not found"));
    }

    public BigDecimal calculateFeeForTransaction(BigDecimal transactionAmount){
        TransactionFee activeFee=getActiveTransactionFee();
        BigDecimal feePercentage = activeFee.getPercentage();
        return transactionAmount.multiply(feePercentage).divide(BigDecimal.valueOf(100));
    }


}
