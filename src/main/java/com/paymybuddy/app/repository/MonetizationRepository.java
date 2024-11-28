package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Monetization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MonetizationRepository extends JpaRepository<Monetization,Integer> {

    Optional<Monetization> findByTransactionId(int transactionID);

    @Query("SELECT SUM(m.result) FROM Monetization m")
    Optional<Long> calculateTotalResult();

}
