package org.airway.airwaybackend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.airway.airwaybackend.dto.EmailSenderDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    String logInUser(LoginDto userDto);
    void createPasswordResetTokenForUser(User user, String token);
    void forgotPassword(EmailSenderDto passwordDto, HttpServletRequest request);

    ResponseEntity<String> resetPassword(String token, ResetPasswordDto passwordDto);
}
