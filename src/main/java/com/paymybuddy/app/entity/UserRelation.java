package com.paymybuddy.app.entity;

import com.paymybuddy.app.entity.id.UserRelationId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "user_relation")
@IdClass(UserRelationId.class)
public class UserRelation {

    @Id
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private int userId;

    @Id
    @Column(name = "user_relation_id", nullable = false, insertable = false, updatable = false)
    private int userRelationId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_relation_id", nullable = false)
    private User relatedUser;

    @Column(name = "status")
    private boolean status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

