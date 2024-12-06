package com.paymybuddy.app.integration;


import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginWebControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppAccountRepository appAccountRepository;


    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);

        user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("$2a$10$testhashedpassword");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        AppAccount appUser = new AppAccount();
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setBalance(0L);
        appUser.setUser(user);
        appAccountRepository.save(appUser);
    }

    @Test
    @WithUserDetails(value = "john.doe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testShowAddRelationPage_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/addRelation"))
                .andExpect(status().isOk())
                .andExpect(view().name("addRelationPage"))
                .andExpect(model().attribute("userId", user.getId()));
    }

    @Test
    @WithUserDetails(value = "john.doe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testShowTransactionPage_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/transaction"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactionPage"))
                .andExpect(model().attribute("userId", user.getId()));
    }

    @Test
    @WithUserDetails(value = "john.doe@example.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testShowProfilePage_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage"))
                .andExpect(model().attribute("userId", user.getId()));
    }


    @Test
    void testShowLoginPage() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    void testShowRegisterPage() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerPage"));
    }

}
