package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.entity.id.UserRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, UserRelationId> {

    Optional<UserRelation> findByUserIdAndUserRelationId(int userId,int userRelationId);
}
