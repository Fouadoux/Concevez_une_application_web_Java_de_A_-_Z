package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    User findByUserName(String userName);
}
