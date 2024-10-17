package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.AppAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppAccountRepository extends CrudRepository<AppAccount,Integer> {
}
