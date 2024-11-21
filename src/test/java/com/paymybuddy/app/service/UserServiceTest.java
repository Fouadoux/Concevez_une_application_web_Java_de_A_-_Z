package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.UpdateUserRequest;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppAccountService appAccountService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("password");
        newUser.setEmail("test@example.com");
        newUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(newUser));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(newUser));

        assertEquals("Email already exists: "+ newUser.getEmail(), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_DefaultRoleNotFound_ThrowsException() {
        User newUser = new User();
        newUser.setUserName("newUser");

        when(userRepository.findByUserName("newUser")).thenReturn(null);
        when(roleRepository.findByRoleName("user")).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.createUser(newUser));

        assertEquals("Default role 'user' not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ValidUser_Success() {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("password");
        newUser.setEmail("test@example.com");
        newUser.setCreatedAt(LocalDateTime.now());

        Role userRole = new Role();
        userRole.setRoleName("USER");


        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Retourner l'utilisateur sauvegardé

        User result = userService.createUser(newUser);


        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals(userRole, newUser.getRole());
        assertNotNull(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void getAllUsers_ReturnsUserList() {
        User user1 = new User();
        user1.setUserName("user1");
        User user2 = new User();
        user2.setUserName("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        User user = new User();
        user.setUserName("user1");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(user, result);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById(1));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateUserRole_RoleNotFound_ThrowsException() {
        User user = new User();
        user.setUserName("user1");

        String role = "admin";

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(role)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserRole(1, "admin"));

        assertEquals("Role not found with role: " + role, exception.getMessage());
    }

    @Test
    void updateUserRole_ValidRole_Success() {
        User user = new User();
        user.setUserName("user1");

        Role adminRole = new Role();
        adminRole.setRoleName("user");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("admin")).thenReturn(Optional.of(adminRole));

        String result = userService.updateUserRole(1, "admin");

        assertEquals("User role updated successfully", result);
        assertEquals(adminRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_UserExists_Success() {
        User user = new User();
        user.setId(1);
        user.setUserName("testUser");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = userService.deleteUser(1);

        assertEquals("User deleted successfully", result);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUser(1));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testUpdateUser_SuccessUserNameOnly(){
        //Arrange
        User user = new User();
        user.setUserName("newUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UpdateUserRequest updateUserRequest=new UpdateUserRequest();
        updateUserRequest.setUserName("Paul");
        // Act
        userService.updateUser(1,updateUserRequest);

        // Assert
        assertEquals("Paul",user.getUserName());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_SuccessEmailOnly(){
        //Arrange
        User user = new User();
        user.setUserName("newUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UpdateUserRequest updateUserRequest=new UpdateUserRequest();
        updateUserRequest.setEmail("Paul@example.fr");
        // Act
        userService.updateUser(1,updateUserRequest);

        // Assert
        assertEquals("newUser",user.getUserName());
        assertEquals("password", user.getPassword());
        assertEquals("Paul@example.fr", user.getEmail());
        verify(userRepository).save(user);
    }
    @Test
    void testUpdateUser_InvalidEmail_ThrowsException(){
        //Arrange
        User user = new User();
        user.setUserName("newUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UpdateUserRequest updateUserRequest=new UpdateUserRequest();
        updateUserRequest.setEmail("invalid-email");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(1, updateUserRequest));

        // Vérifier le message de l'exception
        assertEquals("Invalid email format: invalid-email", exception.getMessage());

        // Vérifier que la méthode save n'a pas été appelée
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_SuccessPasswordOnly(){
        //Arrange
        User user = new User();
        user.setUserName("newUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UpdateUserRequest updateUserRequest=new UpdateUserRequest();
        updateUserRequest.setPassword("newPass");
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        // Act
        userService.updateUser(1,updateUserRequest);

        // Assert
        assertEquals("newUser",user.getUserName());
        assertEquals("encodedNewPass", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_SuccessUpdateAllFields() {
        // Arrange
        User user = new User();
        user.setUserName("oldUser");
        user.setPassword("oldPassword");
        user.setEmail("old@example.com");
        user.setCreatedAt(LocalDateTime.now());

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserName("newUser");
        updateUserRequest.setEmail("new@example.com");
        updateUserRequest.setPassword("newPassword");

        // Mock le comportement du repository et de l'encodeur de mot de passe
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        userService.updateUser(1, updateUserRequest);

        // Assert
        assertEquals("newUser", user.getUserName(), "Username should be updated");
        assertEquals("new@example.com", user.getEmail(), "Email should be updated");
        assertEquals("encodedNewPassword", user.getPassword(), "Password should be encoded and updated");
        verify(userRepository).save(user); // Vérification de l'appel à save
    }

    @Test
    void testUpdateUser_UserNotFound_ThrowsException() {
        // Arrange
        int userId = 1;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserName("newUser");
        updateUserRequest.setEmail("new@example.com");
        updateUserRequest.setPassword("newPassword");

        // Simuler le comportement du repository : l'utilisateur n'existe pas
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(userId, updateUserRequest)
        );

        // Vérification du message de l'exception
        assertEquals("User not found with ID: " + userId, exception.getMessage());

        // Vérifier que la méthode save n'a pas été appelée
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void testFindByEmail_UserFound_ReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setUserName("TestUser");

        // Simuler le comportement du repository
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNotNull(result, "The returned user should not be null");
        assertEquals(email, result.getEmail(), "The email of the returned user should match");
        assertEquals("TestUser", result.getUserName(), "The username of the returned user should match");
        verify(userRepository).findByEmail(email); // Vérifier que le repository a bien été appelé
    }

    @Test
    void testFindByEmail_UserNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";

        // Simuler le comportement du repository : aucun utilisateur trouvé
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findByEmail(email)
        );

        // Vérifier le message de l'exception
        assertEquals("User not found with email: " + email, exception.getMessage());

        // Vérifier que la méthode du repository a été appelée une fois
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindUsernameByUserId_UserFound_ReturnsUsername() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setUserName("TestUser");

        // Simuler le comportement du repository
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userService.findUsernameByUserId(userId);

        // Assert
        assertNotNull(result, "The returned username should not be null");
        assertEquals("TestUser", result, "The username should match the expected value");
        verify(userRepository).findUserById(userId); // Vérifier que le repository a bien été appelé
    }

    @Test
    void testFindUsernameByUserId_UserNotFound_ThrowsException() {
        // Arrange
        int userId = 1;

        // Simuler le comportement du repository : aucun utilisateur trouvé
        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findUsernameByUserId(userId)
        );

        // Vérifier le message de l'exception
        assertEquals("User not found with id: " + userId, exception.getMessage());

        // Vérifier que la méthode du repository a été appelée une fois
        verify(userRepository).findUserById(userId);
    }

    @Test
    void testRegisterAndCreateAccount_Success() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUserName("newUser");
        user.setEmail("test@example.com");

        Role role=new Role();
        role.setRoleName("USER");


        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1); // Simuler l'attribution d'un ID
            return savedUser;
        });

        AppAccount mockAccount = new AppAccount();
        mockAccount.setUser(user);
        mockAccount.setBalance(100); // Balance initiale

        when(appAccountService.createAccountForUser(user.getId())).thenReturn(mockAccount);

        // Act
        userService.registerAndCreateAccount(user);

        // Assert
        verify(userRepository).save(user); // Vérifier que l'utilisateur a été sauvegardé
        verify(appAccountService).createAccountForUser(user.getId()); // Vérifier que le compte a été créé
    }

    @Test
    void testRegisterAndCreateAccount_UserCreationFails_NoAccountCreated() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); // Simuler un email déjà existant

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerAndCreateAccount(user)
        );

        verify(appAccountService, never()).createAccountForUser(anyInt()); // Vérifier qu'aucun compte n'a été créé
    }

    @Test
    void testRegisterAndCreateAccount_AccountCreationFails_ThrowsException() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUserName("newUser");
        user.setEmail("test@example.com");

        Role role=new Role();
        role.setRoleName("USER");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1); // Simuler l'attribution d'un ID
            return savedUser;
        });

        // Simuler une exception lors de la création du compte
        doThrow(new RuntimeException("Failed to create account")).when(appAccountService).createAccountForUser(user.getId());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.registerAndCreateAccount(user)
        );

        assertEquals("Failed to create account", exception.getMessage());
        verify(userRepository).save(user); // Vérifier que l'utilisateur a été sauvegardé
        verify(appAccountService).createAccountForUser(user.getId()); // Vérifier que la méthode a été appelée
    }

    @Test
    void testExistsByEmail_EmailExists_ReturnsTrue() {
        // Arrange
        String email = "existing@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertTrue(result, "The method should return true when the email exists");
        verify(userRepository).findByEmail(email); // Vérifier que le repository a bien été appelé
    }

    @Test
    void testExistsByEmail_EmailDoesNotExist_ReturnsFalse() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertFalse(result, "The method should return false when the email does not exist");
        verify(userRepository).findByEmail(email); // Vérifier que le repository a bien été appelé
    }

}
