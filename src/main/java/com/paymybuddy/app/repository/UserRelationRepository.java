package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.UserRelation;
import com.paymybuddy.app.model.id.UserRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, UserRelationId> {
}
