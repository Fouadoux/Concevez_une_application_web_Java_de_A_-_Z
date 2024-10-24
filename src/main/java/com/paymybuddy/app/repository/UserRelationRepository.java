package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.entity.id.UserRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, UserRelationId> {
}
