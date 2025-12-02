package main;

import main.model.User;
import main.repository.UserRepository;
import main.service.UserService;
import main.web.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_returnsUser_whenExists() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setUserId(id);

        when(userRepository.findByUserId(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);

        assertEquals(id, result.getUserId());
    }

    @Test
    void findById_throwsException_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByUserId(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.findById(id));

        assertEquals("User does not exist!", ex.getMessage());
    }

    // ---------------------------------------------------------
    // addUser
    // ---------------------------------------------------------
    @Test
    void addUser_createsNewUser_whenNotExists() {
        UUID id = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest();
        request.setId(id);

        when(userRepository.findByUserId(id)).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setUserId(id);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.addUser(request);

        assertEquals(id, result.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_returnsExistingUser_whenUserExists() {
        UUID id = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest();
        request.setId(id);

        User existing = new User();
        existing.setUserId(id);

        when(userRepository.findByUserId(id)).thenReturn(Optional.of(existing));

        User result = userService.addUser(request);

        assertSame(existing, result);
        verify(userRepository, never()).save(any(User.class));
    }
}
