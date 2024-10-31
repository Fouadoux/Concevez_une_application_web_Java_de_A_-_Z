package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppAccountRepository extends CrudRepository<AppAccount, Integer> {
    void flush();

    Optional<AppAccount> findByUser(User user);

    Optional<AppAccount> findByUserId(int userId);
}
