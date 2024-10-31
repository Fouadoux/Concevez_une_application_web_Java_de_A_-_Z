package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);

        if (user ==null){
            throw new UsernameNotFoundException("User not found : "+ username);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(user.getUserName(),
                user.getPassword(),
                Collections.singleton(authority));
    }





}
