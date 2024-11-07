package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findUserById(int userId);

    @Query(value = "SELECT u.id FROM User u WHERE u.email = :email")
    Optional<Integer> findIdByEmail(@Param("email") String email);

}
