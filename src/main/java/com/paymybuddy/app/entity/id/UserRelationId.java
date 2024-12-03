package com.paymybuddy.app.entity.id;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class UserRelationId implements Serializable {
    private int userId;
    private int userRelationId;

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
