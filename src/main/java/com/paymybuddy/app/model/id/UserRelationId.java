package com.paymybuddy.app.model.id;

import java.io.Serializable;
import java.util.Objects;

public class UserRelationId implements Serializable {
    private int userId;
    private int userRelationId;

    public int getUserRelationId() {
        return userRelationId;
    }

    public void setUserRelationId(int userRelationId) {
        this.userRelationId = userRelationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserRelationId() {}

    public UserRelationId(int userId, int userRelationId) {
        this.userId = userId;
        this.userRelationId = userRelationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRelationId that = (UserRelationId) o;
        return userId == that.userId && userRelationId == that.userRelationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userRelationId);
    }
}
