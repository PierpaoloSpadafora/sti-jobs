package unical.demacs.rdm.service;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.config.exception.NoUserFoundException;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.repository.UserRepository;
import unical.demacs.rdm.persistence.service.implementation.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RateLimiter rateLimiter;

    private UserServiceImpl userService;
    private User testUser;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ID = "123";

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, rateLimiter);
        testUser = User.buildUser()
                .email(TEST_EMAIL)
                .build();
    }

    @Test
    void testCreateUser_NewUser() {
        when(userRepository.findByEmail(eq(TEST_EMAIL))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(TEST_EMAIL);

        assertNotNull(createdUser);
        assertEquals(TEST_EMAIL, createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_ExistingUser() {
        when(userRepository.findByEmail(eq(TEST_EMAIL))).thenReturn(Optional.of(testUser));

        User existingUser = userService.createUser(TEST_EMAIL);

        assertNotNull(existingUser);
        assertEquals(TEST_EMAIL, existingUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_ThrowsException() {
        when(userRepository.findByEmail(eq(TEST_EMAIL))).thenThrow(new RuntimeException());

        assertThrows(UserException.class, () -> userService.createUser(TEST_EMAIL));
    }

    @Test
    void testGetUserByEmail_Found() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(userRepository.findByEmail(eq(TEST_EMAIL))).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getUserByEmail(TEST_EMAIL);

        assertTrue(foundUser.isPresent());
        assertEquals(TEST_EMAIL, foundUser.get().getEmail());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(userRepository.findByEmail(eq(TEST_EMAIL))).thenReturn(Optional.empty());

        assertThrows(NoUserFoundException.class, () -> userService.getUserByEmail(TEST_EMAIL));
    }

    @Test
    void testGetUserByEmail_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> userService.getUserByEmail(TEST_EMAIL));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void testGetUserById_Found() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getUserById(TEST_ID);

        assertTrue(foundUser.isPresent());
        assertEquals(TEST_EMAIL, foundUser.get().getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(NoUserFoundException.class, () -> userService.getUserById(TEST_ID));
    }

    @Test
    void testGetUserById_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> userService.getUserById(TEST_ID));
        verify(userRepository, never()).findById((String) any());
    }

    @Test
    void testDeleteUserById_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));

        assertDoesNotThrow(() -> userService.deleteUserById(TEST_ID));
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUserById_NotFound() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        assertThrows(NoUserFoundException.class, () -> userService.deleteUserById(TEST_ID));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testDeleteUserById_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> userService.deleteUserById(TEST_ID));
        verify(userRepository, never()).findById((String) any());
        verify(userRepository, never()).delete(any());
    }
}