package org.airway.airwaybackend.serviceImpl;

import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        // Arrange
        String userEmail = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(userEmail);

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock);

        // Act
        UserDetails userDetails = userService.loadUserByUsername(userEmail);

        // Assert
        assertNotNull(userDetails);
        assertEquals(userEmail, userDetails.getUsername());

        // Verify that the repository method was called with the correct email
        verify(userRepositoryMock, times(1)).findByEmail(userEmail);
        // Ensure that no other methods of the mock were called
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        // Arrange
        String userEmail = "nonexistent@example.com";

        UserRepository userRepositoryMock = mock(UserRepository.class);
        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(Optional.empty());

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(userEmail));

        // Verify that the repository method was called with the correct email
        verify(userRepositoryMock, times(1)).findByEmail(userEmail);
        // Ensure that no other methods of the mock were called
        verifyNoMoreInteractions(userRepositoryMock);
    }
}
