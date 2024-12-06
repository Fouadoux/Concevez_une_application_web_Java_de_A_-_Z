package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.UpdateUserRequestDTO;
import com.paymybuddy.app.dto.UserDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EmailAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidEmailException;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void createUser_DefaultRoleNotFound_ThrowsException() {
        // Arrange
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password123");

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.createUser(newUser));

        assertEquals("Default role 'USER' not found", exception.getMessage());
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

        assertEquals("Role not found with name: " + role, exception.getMessage());
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

        assertEquals("User role updated successfully.", result);
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

        assertEquals("User deleted successfully.", result);
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
        UpdateUserRequestDTO updateUserRequestDTO =new UpdateUserRequestDTO();
        updateUserRequestDTO.setUserName("Paul");
        // Act
        userService.updateUser(1, updateUserRequestDTO);

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
        UpdateUserRequestDTO updateUserRequestDTO =new UpdateUserRequestDTO();
        updateUserRequestDTO.setEmail("Paul@example.fr");
        // Act
        userService.updateUser(1, updateUserRequestDTO);

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
        UpdateUserRequestDTO updateUserRequestDTO =new UpdateUserRequestDTO();
        updateUserRequestDTO.setEmail("invalid-email");

        // Act & Assert
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () ->
                userService.updateUser(1, updateUserRequestDTO));

        assertEquals("Invalid email format: invalid-email", exception.getMessage());

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
        UpdateUserRequestDTO updateUserRequestDTO =new UpdateUserRequestDTO();
        updateUserRequestDTO.setPassword("newPass");
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        // Act
        userService.updateUser(1, updateUserRequestDTO);

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

        UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO();
        updateUserRequestDTO.setUserName("newUser");
        updateUserRequestDTO.setEmail("new@example.com");
        updateUserRequestDTO.setPassword("newPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        userService.updateUser(1, updateUserRequestDTO);

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
        UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO();
        updateUserRequestDTO.setUserName("newUser");
        updateUserRequestDTO.setEmail("new@example.com");
        updateUserRequestDTO.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(userId, updateUserRequestDTO)
        );

        assertEquals("User not found with ID: " + userId, exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void testGetUserByEmail_UserFound_ReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setUserName("TestUser");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result, "The returned user should not be null");
        assertEquals(email, result.getEmail(), "The email of the returned user should match");
        assertEquals("TestUser", result.getUserName(), "The username of the returned user should match");
        verify(userRepository).findByEmail(email); // Vérifier que le repository a bien été appelé
    }

    @Test
    void testGetUserByEmail_UserNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByEmail(email)
        );

        assertEquals("User not found with email: " + email, exception.getMessage());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindUsernameByUserId_UserFound_ReturnsUsername() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setUserName("TestUser");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userService.findUsernameByUserId(userId);

        // Assert
        assertNotNull(result, "The returned username should not be null");
        assertEquals("TestUser", result, "The username should match the expected value");

        verify(userRepository, times(1)).findUserById(userId);
    }

    @Test
    void testFindUsernameByUserId_UserNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        int userId = 1;

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findUsernameByUserId(userId),
                "Expected EntityNotFoundException to be thrown");

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findUserById(userId);
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
            savedUser.setId(1);
            return savedUser;
        });

        AppAccount mockAccount = new AppAccount();
        mockAccount.setUser(user);
        mockAccount.setBalance(100);

        when(appAccountService.createAccountForUser(user.getId())).thenReturn(mockAccount);

        // Act
        userService.registerAndCreateAccount(user);

        // Assert
        verify(userRepository).save(user);
        verify(appAccountService).createAccountForUser(user.getId());
    }

    @Test
    void testRegisterAndCreateAccount_UserCreationFails_NoAccountCreated() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); // Simuler un email déjà existant

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () ->
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
            savedUser.setId(1);
            return savedUser;
        });

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

    @Test
    void testConvertToDTO() {
        User user = mock(User.class);
        Role role = mock(Role.class);

        when(user.getUserName()).thenReturn("John Doe");
        when(user.getEmail()).thenReturn("john.doe@example.com");
        when(role.getRoleName()).thenReturn("ADMIN");
        when(user.getRole()).thenReturn(role);
        when(user.getCreatedAt()).thenReturn(LocalDateTime.of(2023, 11, 27, 10, 30));

        UserDTO userDTO = userService.convertToDTO(user);

        assertNotNull(userDTO);
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        assertEquals("ADMIN", userDTO.getRole());
        assertEquals(LocalDateTime.of(2023, 11, 27, 10, 30), userDTO.getCreatedAt());
    }

    @Test
    void testConvertToDTOList() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        Role role = mock(Role.class);
        when(user1.getUserName()).thenReturn("User1");
        when(user1.getEmail()).thenReturn("user1@example.com");
        when(user1.getRole()).thenReturn(role);
        when(role.getRoleName()).thenReturn("USER");
        when(user1.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(user2.getUserName()).thenReturn("User2");
        when(user2.getEmail()).thenReturn("user2@example.com");
        when(user2.getRole()).thenReturn(role);
        when(role.getRoleName()).thenReturn("ADMIN");
        when(user2.getCreatedAt()).thenReturn(LocalDateTime.now());

        List<User> users = Arrays.asList(user1, user2);

        List<UserDTO> userDTOList = userService.convertToDTOList(users);

        assertNotNull(userDTOList);
        assertEquals(2, userDTOList.size());
        assertEquals("User1", userDTOList.get(0).getName());
        assertEquals("User2", userDTOList.get(1).getName());
    }

    @Test
    void testGetFindByRole() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);

        Role adminRole = mock(Role.class);
        Role userRole = mock(Role.class);

        when(user1.getRole()).thenReturn(adminRole);
        when(adminRole.getRoleName()).thenReturn("ADMIN");

        when(user2.getRole()).thenReturn(userRole);
        when(userRole.getRoleName()).thenReturn("USER");

        when(user3.getRole()).thenReturn(adminRole);

        List<User> users = Arrays.asList(user1, user2, user3);

        List<UserDTO> adminUsers = userService.getFindByRole(users, "ADMIN");

        assertNotNull(adminUsers);
        assertEquals(2, adminUsers.size());
        assertEquals("ADMIN", adminUsers.get(0).getRole());
        assertEquals("ADMIN", adminUsers.get(1).getRole());
    }
    @Test
    void softDeleteUser_Success() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        String result = userService.softDeleteUser(userId);

        // Assert
        assertEquals("User soft deleted successfully.", result);
        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteUser_AlreadyDeleted() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        String result = userService.softDeleteUser(userId);

        // Assert
        assertEquals("User is already soft deleted.", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void softDeleteUser_UserNotFound() {
        // Arrange
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.softDeleteUser(userId);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void cancelSoftDeleteUser_Success() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        String result = userService.cancelSoftDeleteUser(userId);

        // Assert
        assertEquals("User soft delete has been canceled.", result);
        assertFalse(user.isDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void cancelSoftDeleteUser_NotDeleted() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);
        user.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        String result = userService.cancelSoftDeleteUser(userId);

        // Assert
        assertEquals("User is not marked as deleted.", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void cancelSoftDeleteUser_UserNotFound() {
        // Arrange
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.cancelSoftDeleteUser(userId);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
