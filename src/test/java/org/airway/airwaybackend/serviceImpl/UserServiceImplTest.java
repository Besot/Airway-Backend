package org.airway.airwaybackend.serviceImpl;

import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.exception.PasswordsDontMatchException;
import org.airway.airwaybackend.exception.UserNotVerifiedException;
import org.airway.airwaybackend.model.PasswordResetToken;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.PasswordResetTokenRepository;
import org.airway.airwaybackend.repository.UserRepository;
import org.airway.airwaybackend.repository.VerificationTokenRepository;
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
    @Mock
    private EmailServiceImpl emailService;

    @MockBean
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    AutoCloseable autoCloseable ;

    @BeforeEach
    public void setup() {
        autoCloseable=  MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository,jwtUtils, passwordEncoder, passwordResetTokenRepository, emailService, (VerificationTokenRepository) userService);
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
        UserServiceImpl userServiceImplMock = mock(UserServiceImpl.class);
        PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        JwtUtils jwtUtilsMock = mock(JwtUtils.class);
        EmailServiceImpl emailServiceMock = mock(EmailServiceImpl.class);
        PasswordResetTokenRepository passwordResetTokenRepositoryMock = mock(PasswordResetTokenRepository.class);

        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock,jwtUtilsMock, passwordEncoderMock, passwordResetTokenRepositoryMock, emailServiceMock, (VerificationTokenRepository) userServiceImplMock);

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
        EmailServiceImpl emailServiceMock = mock(EmailServiceImpl.class);
        UserServiceImpl userServiceImplMock = mock(UserServiceImpl.class);
        PasswordResetTokenRepository passwordResetTokenRepositoryMock = mock(PasswordResetTokenRepository.class);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, jwtUtilsMock, passwordEncoderMock, passwordResetTokenRepositoryMock, emailServiceMock, (VerificationTokenRepository) userServiceImplMock);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(userEmail));

        // Verify that the repository method was called with the correct email
        verify(userRepositoryMock, times(1)).findByEmail(userEmail);
        // Ensure that no other methods of the mock were called
        verifyNoMoreInteractions(userRepositoryMock);
    }

    
}
