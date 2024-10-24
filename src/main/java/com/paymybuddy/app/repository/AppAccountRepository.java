package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppAccountRepository extends CrudRepository<AppAccount,Integer> {
    void flush();
    AppAccount findByUser(User user);
}
