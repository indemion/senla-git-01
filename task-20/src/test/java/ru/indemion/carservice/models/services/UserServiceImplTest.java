package ru.indemion.carservice.models.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.indemion.carservice.exceptions.EntityNotFoundException;
import ru.indemion.carservice.models.auth.Role;
import ru.indemion.carservice.models.auth.User;
import ru.indemion.carservice.models.repositories.UserRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {
        // Arrange
        String username = "john_doe";
        String password = "secret";
        Role roleUser = new Role("ROLE_USER");
        Role roleAdmin = new Role("ROLE_ADMIN");
        Set<Role> roles = Set.of(roleUser, roleAdmin);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(roles);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                .containsAll(Set.of("ROLE_USER", "ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.loadUserByUsername(username)
        );
        assertTrue(exception.getMessage().contains("User с username: " + username + " не найден"));
    }
}