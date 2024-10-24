package com.paymybuddy.app.entity;

import com.paymybuddy.app.entity.id.UserRelationId;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_relation")
@IdClass(UserRelationId.class)
public class UserRelation {

    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Id
    @Column(name = "user_relation_id", nullable = false)
    private int userRelationId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "status")
    private boolean status;

    @Column(name = "created_at")
    private Date createdAt;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserRelationId() {
        return userRelationId;
    }

    public void setUserRelationId(int userRelationId) {
        this.userRelationId = userRelationId;
    }

    public User getAppUser() {
        return user;
    }

    public void setAppUser(User user) {
        this.user = user;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
