package org.airway.airwaybackend.serviceImpl;

import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.exception.PasswordsDontMatchException;
import org.airway.airwaybackend.exception.UserNotVerifiedException;
import org.airway.airwaybackend.model.PasswordResetToken;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.PasswordResetTokenRepository;
import org.airway.airwaybackend.repository.UserRepository;
import org.airway.airwaybackend.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    AutoCloseable autoCloseable ;

    @BeforeEach
    public void setup() {
        autoCloseable=  MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository,jwtUtils, passwordEncoder, passwordResetTokenRepository);
    }
    @Test
    void testLoginUser_UsrNotVerified() {
        User mockUser = new User();
        mockUser.setEmail("test@gmail.com");
        mockUser.setIsEnabled(false);
        mockUser.setPassword(passwordEncoder.encode("1234"));

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(mockUser));
        jwtUtils = mock(JwtUtils.class);


        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@gmail.com");
        loginDto.setPassword("1234");


        assertThrows(UserNotVerifiedException.class, () -> userService.logInUser(loginDto));
        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        assertFalse(mockUser.getIsEnabled());
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        // Arrange
        String userEmail = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(userEmail);


        UserRepository userRepositoryMock = mock(UserRepository.class);
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        JwtUtils jwtUtilsMock = mock(JwtUtils.class);
        PasswordResetTokenRepository passwordResetTokenRepositoryMock = mock(PasswordResetTokenRepository.class);

        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock,jwtUtilsMock, passwordEncoderMock, passwordResetTokenRepositoryMock);

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
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        JwtUtils jwtUtilsMock = mock(JwtUtils.class);
        PasswordResetTokenRepository passwordResetTokenRepositoryMock = mock(PasswordResetTokenRepository.class);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, jwtUtilsMock, passwordEncoderMock, passwordResetTokenRepositoryMock);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(userEmail));

        // Verify that the repository method was called with the correct email
        verify(userRepositoryMock, times(1)).findByEmail(userEmail);
        // Ensure that no other methods of the mock were called
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    void testChangePassword_PasswordsMatch_SaveUser() {
        // Arrange
        User mockUser = new User();
        String newPassword = "newPassword";
        String newConfirmPassword = "newPassword";

        // Act
        userService.changePassword(mockUser, newPassword, newConfirmPassword);

        // Verify
        verify(userRepository, times(1)).save(mockUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testChangePassword_PasswordsMismatch_ThrowsException() {
        // Arrange
        User mockUser = new User();
        String newPassword = "newPassword";
        String newConfirmPassword = "differentPassword";

        // Act and Assert
        assertThrows(PasswordsDontMatchException.class, () -> userService.changePassword(mockUser, newPassword, newConfirmPassword));

        // Verify
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetUserByPasswordReset_ValidToken_ReturnsUser() {
        // Arrange
        String token = "validToken";
        User mockUser = new User();
        PasswordResetToken mockToken = new PasswordResetToken();
        mockToken.setUser(mockUser);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(mockToken);

        // Act
        Optional<User> result = userService.getUserByPasswordReset(token);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());

        // Verify
        verify(passwordResetTokenRepository, times(1)).findByToken(token);
        verifyNoMoreInteractions(passwordResetTokenRepository);
    }
}
