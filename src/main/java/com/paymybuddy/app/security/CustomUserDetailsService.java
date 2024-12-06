package com.paymybuddy.app.security;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if(user.isDeleted()){
            throw new UsernameNotFoundException("This account has been deleted.");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName());
        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(authority),
                user.getId(),
                user.getUserName()
        );
    }






}
