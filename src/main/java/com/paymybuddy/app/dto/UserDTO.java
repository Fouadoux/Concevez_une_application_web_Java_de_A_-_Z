package com.paymybuddy.app.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter

public class UserDTO {

    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    public UserDTO(String name, String email, String role, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UserDTO() {
    }
}
