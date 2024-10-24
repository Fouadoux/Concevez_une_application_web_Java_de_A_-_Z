package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.TransactionFee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionFeeRepository extends CrudRepository<TransactionFee,Integer> {
    Optional<TransactionFee> findTopByOrderByEffectiveDateDesc();
}
