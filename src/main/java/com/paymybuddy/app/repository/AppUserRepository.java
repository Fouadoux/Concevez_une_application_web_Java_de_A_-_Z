package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser,Integer> {
}
