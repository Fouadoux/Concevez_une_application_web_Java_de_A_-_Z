package com.paymybuddy.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRelationDTO {

    private int userId;
    private int userRelationId;
    private boolean status;
    private LocalDateTime createdAt;
}
