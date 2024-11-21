package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserSenderOrUserReceiverAndTransactionDateBetween(User userSender, User userReceiver, LocalDateTime transactionDate, LocalDateTime transactionDate2);

    //Total des montants envoyés
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userSender = :user AND t.transactionDate BETWEEN :startDate AND :endDate")
    Long calculateTotalSentByUserAndDateRange(@Param("user") User user,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    //Total des montants reçus
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userReceiver = :user AND t.transactionDate BETWEEN :startDate AND :endDate")
    Long calculateTotalReceivedByUserAndDateRange(@Param("user") User user,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);


}
