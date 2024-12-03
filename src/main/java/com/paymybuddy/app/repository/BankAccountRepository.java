package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount,Integer> {
   Optional< List<BankAccount>>findAllBankAccountByUser (User user);
}
