package org.airway.airwaybackend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.airway.airwaybackend.dto.EmailSenderDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.dto.SignupDto;
import org.airway.airwaybackend.model.User;

import java.util.Optional;

public interface UserService {
    String logInUser(LoginDto userDto);
    void createPasswordResetTokenForUser(User user, String token);
    void forgotPassword(EmailSenderDto passwordDto, HttpServletRequest request);
    User saveUser(SignupDto signupDto);

    void saveVerificationTokenForUser(User user, String token);
}
