package com.paymybuddy.app.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {

    private final int id;
    private final String nameUser;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int id, String nameUser) {
        super(username, password, authorities);
        this.id = id;
        this.nameUser = nameUser;
    }

}

