package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppAccountRepository extends JpaRepository<AppAccount, Integer> {
    Optional<AppAccount> findByUserId(int userId);
}
