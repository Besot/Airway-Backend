package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.model.User;

import java.util.Optional;

public interface UserService {
    String logInUser(LoginDto userDto);
    void createPasswordResetTokenForUser(User user, String token);
    String validatePasswordResetToken(String token, ResetPasswordDto passwordDto);
    Optional<User> getUserByPasswordReset(String token);
    void changePassword(User user, String newPassword, String newConfirmPassword);
}
