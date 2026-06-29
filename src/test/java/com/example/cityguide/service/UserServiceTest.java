package com.example.cityguide.service;

import com.example.cityguide.model.User;
import com.example.cityguide.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("test_user");
        testUser.setPassword("raw_password");
    }

    @Test
    void getAllUsers_Success_ShouldReturnListOfUsers() {

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setLogin("admin");

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, secondUser));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("test_user", users.get(0).getLogin());
        assertEquals("admin", users.get(1).getLogin());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_Success_ShouldReturnUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test_user", result.getLogin());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound_ShouldThrowException() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(999L);
        });

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void register_Success_ShouldHashPasswordAndSetRole() {

        when(userRepository.findByLogin("test_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw_password")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.register(testUser);

        assertNotNull(registeredUser);
        assertEquals("hashed_password", registeredUser.getPassword());
        assertEquals("USER", registeredUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void register_UserAlreadyExists_ShouldThrowException() {

        when(userRepository.findByLogin("test_user")).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(testUser);
        });

        assertEquals("Пользователь с таким логином уже существует", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success_ShouldReturnUser() {

        testUser.setPassword("hashed_password");
        when(userRepository.findByLogin("test_user")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("raw_password", "hashed_password")).thenReturn(true);

        User loggedInUser = userService.login("test_user", "raw_password");

        assertNotNull(loggedInUser);
        assertEquals("test_user", loggedInUser.getLogin());
    }

    @Test
    void login_WrongPassword_ShouldThrowException() {

        testUser.setPassword("hashed_password");
        when(userRepository.findByLogin("test_user")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("test_user", "wrong_password");
        });

        assertEquals("Неверный логин или пароль", exception.getMessage());
    }

    @Test
    void deleteUser_Success_ShouldInvokeRepositoryDelete() {

        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ShouldThrowException() {

        when(userRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }
}