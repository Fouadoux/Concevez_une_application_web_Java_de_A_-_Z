package com.paymybuddy.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/image/**").permitAll() // Autoriser l'accès aux ressources publiques
                        .anyRequest().authenticated() // Tout le reste nécessite une authentification
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL de votre page de login personnalisée
                        .defaultSuccessUrl("/transaction", true) // Rediriger vers "/connected" après une connexion réussie
                        .permitAll() // Autoriser tout le monde à accéder à la page de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL pour la déconnexion
                        .logoutSuccessUrl("/login") // Rediriger vers la page de login après la déconnexion
                        .permitAll() // Autoriser tout le monde à se déconnecter
                )

                .build();
    }




    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
