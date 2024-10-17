package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.TransactionFee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionFeeRepository extends CrudRepository<TransactionFee,Integer> {
}
