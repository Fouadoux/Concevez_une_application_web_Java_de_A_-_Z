package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.BankAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount,Integer> {
}
