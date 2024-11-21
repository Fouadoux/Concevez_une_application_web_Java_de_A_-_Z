package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidTransactionFeeException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.TransactionFeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class TransactionFeeService {

    private final TransactionFeeRepository transactionFeeRepository;

    public TransactionFeeService(TransactionFeeRepository transactionFeeRepository) {
        this.transactionFeeRepository = transactionFeeRepository;
    }

    public TransactionFee createTransactionFee(TransactionFee fee) {
        if (fee.getPercentage() == 0) {
            throw new InvalidTransactionFeeException("The transaction fee percentage must be greater than zero.");
        }

        fee.setEffectiveDate(LocalDateTime.now());

        try {
            return transactionFeeRepository.save(fee); // Return the saved object
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save transaction fee.", e);
        }
    }

    public TransactionFee getActiveTransactionFee() {
        return transactionFeeRepository.findTopByOrderByEffectiveDateDesc()
                .orElseThrow(() -> new EntityNotFoundException("No active transaction fee found."));
    }

    public TransactionFee updateTransactionFeePercentage(int id, long newPercentage) {
        if (newPercentage ==  0) {
            throw new InvalidTransactionFeeException("The transaction fee percentage must be greater than zero.");
        }

        return transactionFeeRepository.findById(id).map(fee -> {
            fee.setPercentage(newPercentage);
            return transactionFeeRepository.save(fee); // Return the updated object
        }).orElseThrow(() -> new EntityNotFoundException("Transaction fee not found with ID: " + id));
    }

    public void deleteTransactionFee(int id) {
        TransactionFee feeToDelete = transactionFeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction fee not found with ID: " + id));

        try{
            transactionFeeRepository.delete(feeToDelete);
        }catch (Exception e){
            throw new EntityDeleteException("failed delete transaction fee wiith ID: "+feeToDelete.getFeeId(),e);
        }

    }

    public long calculateFeeForTransaction(long transactionAmount) {
        TransactionFee activeFee = getActiveTransactionFee();

        if (transactionAmount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        // Récupérer le pourcentage des frais (par exemple, 2.5 pour 2.5%)
        double feePercentage = activeFee.getPercentage();

        // Calcul des frais (en centimes)
        return Math.round(transactionAmount * (feePercentage / 100.0));
    }
}
